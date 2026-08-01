#!/usr/bin/env bash
set -euo pipefail

loader="${1:?usage: smoke-server.sh <fabric|neoforge>}"
run_dir="${loader}/run/server"
gradle_log="${run_dir}/gradle.log"
server_log="${run_dir}/logs/latest.log"

mkdir -p "${run_dir}"
printf 'eula=true\n' > "${run_dir}/eula.txt"

./gradlew ":${loader}:runServer" --no-daemon --max-workers=2 >"${gradle_log}" 2>&1 &
server_pid=$!

cleanup() {
    kill "${server_pid}" 2>/dev/null || true
    wait "${server_pid}" 2>/dev/null || true
}
trap cleanup EXIT

for _ in $(seq 1 180); do
    if [[ -f "${server_log}" ]] && grep -Eq 'Done \([0-9.]+s\)! For help, type "help"' "${server_log}"; then
        exit 0
    fi
    if [[ -f "${server_log}" ]] && grep -Eq 'Mixin apply failed|MixinTransformerError|NoClassDefFoundError|Exception in thread "main"' "${server_log}"; then
        break
    fi
    if ! kill -0 "${server_pid}" 2>/dev/null; then
        break
    fi
    sleep 1
done

printf '%s\n' "Dedicated server smoke test failed for ${loader}" >&2
[[ -f "${gradle_log}" ]] && cp "${gradle_log}" /dev/stderr
[[ -f "${server_log}" ]] && cp "${server_log}" /dev/stderr
exit 1
