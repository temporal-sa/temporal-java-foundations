# Prerequisites — Temporal Debug Build

[English](./PREREQUISITES.md) · [한국어](./PREREQUISITES_ko_kr.md)

Do this **before** starting the labs in [`README.md`](./README.md). End-to-end from
`git clone` to a running **debug** Temporal server. Covers **macOS**, **Windows (WSL2)**,
and **Linux**. **No prior Go experience needed.**

> **Why not `temporal server start-dev`?** The Lab 5 determinism exercise (and holding
> breakpoints in workflow code) needs a server that won't give up on the worker while you
> pause. The **debug build** compiles with the `TEMPORAL_DEBUG` build tag, which multiplies
> the server's internal timeouts ×100 — that slack is exactly what keeps a paused worker
> alive. It also ships the same Docker stack whose **Web UI (:8080)** you'll use throughout
> the labs to read event history.

---

## 0. What you'll build

The `temporal-server-debug` program from Temporal's source code (the Temporal server in
debug mode), plus the databases and Web UI it needs, started with Docker. You'll also build
`tdbg` (decodes raw data in the database) — **optional, for advanced users**; the labs are
driven from the **Web UI**.

> **You will NOT write any Go.** `make` runs the build for you and Go compiles
> automatically. You only need Go *installed*.

---

## 1. System requirements

The database stack (MySQL, Elasticsearch, Cassandra, and more) runs in Docker and is
memory-hungry. Have **≥ 8 GB RAM free** (allocate ≥ 6–8 GB to Docker Desktop), **~ 15 GB
free disk**, and internet (the first build downloads several hundred MB).

---

## 2. Install the tools

You need four tools: **Git**, **Go 1.26.4+**, **GNU Make**, **Docker**. The **Temporal CLI**
is a handy extra (the lab scripts use it).

### macOS

Easiest via [Homebrew](https://brew.sh). `xcode-select --install` gives Git + Make.

```bash
# 1) Xcode Command Line Tools (gives you git + make)
xcode-select --install

# 2) Homebrew (skip if already installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 3) Go, plus (optional) the Temporal CLI and a newer GNU Make
brew install go temporal make

# 4) Docker Desktop — download, install, and LAUNCH it once:
#    https://www.docker.com/products/docker-desktop/  (Apple Silicon & Intel both work)
```

> **Apple Silicon:** In Docker Desktop, keep "Use Rosetta for x86/amd64 emulation" enabled
> (Settings → General) for the widest image compatibility.

### Windows (WSL2) / Linux

**Windows first:** Temporal builds inside Linux. Install **WSL2 + Ubuntu**, then do
*everything else* inside the Ubuntu terminal. Open **PowerShell as Administrator**:

```powershell
# Windows PowerShell (Administrator) — installs WSL2 + Ubuntu, then reboot
wsl --install
```

**Ubuntu / WSL2 / Linux:** Ubuntu's packaged Go is usually too old — install the official
one. (ARM machines: replace `amd64` with `arm64`.)

```bash
# 1) Git + GNU Make
sudo apt update && sudo apt install -y git make curl

# 2) Go 1.26.4 (official) — remove old Go, install to /usr/local
curl -LO https://go.dev/dl/go1.26.4.linux-amd64.tar.gz
sudo rm -rf /usr/local/go && sudo tar -C /usr/local -xzf go1.26.4.linux-amd64.tar.gz

# 3) Put Go on your PATH (add this line to ~/.profile, then re-open the shell)
export PATH=$PATH:/usr/local/go/bin

# 4) (optional) Temporal CLI
curl -sSf https://temporal.download/cli.sh | sh
```

**Docker.** *Windows (WSL2):* install
[Docker Desktop for Windows](https://www.docker.com/products/docker-desktop/) and enable
Settings → Resources → WSL Integration for Ubuntu. *Native Linux:* install Docker Engine:

```bash
# Native Linux only (skip on Windows/WSL2 — Docker Desktop handles it)
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER      # then log out and back in
```

### ✔ Verify

```bash
git --version
go version            # your installed Go; the build auto-fetches 1.26.4 if needed
make --version
docker --version
docker compose version
```

> **Go version:** If `go version` is older than 1.26.4, that's usually fine — modern Go
> auto-downloads the toolchain pinned in `go.mod` on the first build.

---

## 3. Get the source code

Clone the Temporal server repo and use `main` as-is — **no source changes needed**. All
later commands run from this folder. **On WSL2, clone inside the Linux home (`~`), not
`/mnt/c`** — far faster.

```bash
git clone https://github.com/temporalio/temporal.git
cd temporal
```

> **Remember where you cloned it.** Run the server commands below from this folder. The
> advanced lab scripts (`history.sh`, `describe.sh`, `sql.sh`) also look here — at
> `~/temporal-oss/temporal` by default; set `TEMPORAL_SRC=/path/to/temporal` if yours is
> elsewhere.

---

## 4. Build the binaries

The **first build is slow** (downloads all Go packages, may fetch the Go 1.26.4 toolchain)
— several minutes is normal. Later builds are fast.

```bash
make temporal-server-debug     # -> ./temporal-server-debug (debug build: relaxed timeouts)
make temporal-sql-tool         # -> ./temporal-sql-tool (installs the DB schema)
make tdbg                      # -> ./tdbg  (OPTIONAL: only for the advanced CLI scripts)
```

**✔ Verify**

```bash
ls -la temporal-server-debug temporal-sql-tool   # required
./temporal-server-debug --help | head
```

---

## 5. Start the databases — **Terminal 1**

Starts MySQL, the Temporal Web UI, and more via Docker Compose. It keeps running — **leave
this terminal open**. Make sure Docker Desktop is running first.

```bash
make start-dependencies         # MySQL :3306, Temporal UI :8080, Grafana, ...
# wait until MySQL logs "ready for connections"
```

---

## 6. Install the schema — **Terminal 2**

Create the Temporal tables in MySQL. Run once (re-run any time for a clean DB).

```bash
cd temporal
make install-schema-mysql        # creates the `temporal` + `temporal_visibility` DBs
```

---

## 7. Run the debug server — **Terminal 3**

Start the server you built, pointed at MySQL. It keeps running — leave it open.

```bash
./temporal-server-debug \
  --config-file config/development-mysql8.yaml \
  --allow-no-auth start
# gRPC on :7233 · Web UI on http://localhost:8080
```

---

## 8. Verify everything works — **Terminal 4**

```bash
temporal operator namespace create --namespace default   # ignore "already exists"
temporal operator cluster health                          # should print SERVING
# open the dashboard: http://localhost:8080
```

> ✅ **Done** — If the namespace exists and the Web UI loads at http://localhost:8080, your
> environment is ready. Head to [`README.md`](./README.md) and start Lab 1. 🎉

---

## 9. Troubleshooting

| Symptom | Fix |
|---------|-----|
| `make: command not found` | Make isn't installed — see step 2. |
| `Cannot connect to the Docker daemon` | Start Docker Desktop (Linux: `sudo systemctl start docker`). |
| Build fails on Go version | Ensure internet so Go can fetch the 1.26.4 toolchain; `go version` ≥ 1.21. |
| `port ... already in use` (7233/8080/3306) | Another Temporal/DB is running — stop it. |
| WSL2 build extremely slow | Clone under the Linux home `~`, never `/mnt/c/...`. |
| Docker runs out of memory | Raise Docker Desktop memory to ≥ 8 GB (Settings → Resources). |
| `temporal: command not found` | The lab scripts use the Temporal CLI — install it (step 2). |
| Advanced scripts can't find `tdbg` | `tdbg` is optional — `cd temporal && make tdbg`, or set `export TEMPORAL_SRC=/path/to/temporal`. |

### Teardown

Press `Ctrl+C` in the server terminal, then stop Docker:

```bash
make stop-dependencies
```

---

*Reference: the Temporal repo's `CONTRIBUTING.md` and `Makefile`. Go `1.26.4`, no C compiler
required (`CGO_ENABLED=0`).*
