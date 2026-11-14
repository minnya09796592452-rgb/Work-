#!/usr/bin/env python3
"""Build and run the CASTAWAY DEPTHS direct Android Vulkan queue/device probe."""

from __future__ import annotations

import argparse
import os
from pathlib import Path
import shlex
import subprocess
import sys


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--output",
        default=str(Path.home() / "CASTAWAY_VULKAN_DEVICE_TEST.txt"),
        help="where to write the combined build/runtime report",
    )
    parser.add_argument(
        "--source",
        default=str(Path(__file__).resolve().parents[1] / "native/vulkan/vulkan_device_probe.cpp"),
        help="C++ probe source path",
    )
    parser.add_argument(
        "--binary",
        default=str(Path.home() / ".cache/castaway_vulkan_device_probe/vulkan_device_probe"),
        help="compiled executable path",
    )
    args = parser.parse_args()

    source = Path(args.source).expanduser().resolve()
    binary = Path(args.binary).expanduser().resolve()
    output = Path(args.output).expanduser()
    binary.parent.mkdir(parents=True, exist_ok=True)
    output.parent.mkdir(parents=True, exist_ok=True)

    prefix = os.environ.get("PREFIX", "")
    include_dir = Path(prefix) / "include" if prefix else None
    compiler = os.environ.get("CXX", "clang++")
    command = [compiler, "-std=c++17", "-Wall", "-Wextra", "-Wpedantic"]
    if include_dir and (include_dir / "vulkan/vulkan.h").is_file():
        command += ["-I", str(include_dir)]
    command += [str(source), "-ldl", "-o", str(binary)]

    lines = [
        "===== CASTAWAY VULKAN QUEUE / LOGICAL DEVICE TEST =====",
        f"PYTHON={sys.version.split()[0]}",
        f"SOURCE={source}",
        f"BINARY={binary}",
        f"OUTPUT={output}",
        "COMPILE_COMMAND=" + shlex.join(command),
        "",
    ]

    if not source.is_file():
        lines.append("COMPILE_EXIT=SOURCE_NOT_FOUND")
        output.write_text("\n".join(lines) + "\n", encoding="utf-8")
        print(f"Report: {output}")
        return 2

    try:
        build = subprocess.run(command, text=True, capture_output=True, check=False)
    except FileNotFoundError as exc:
        lines.extend([f"COMPILE_LAUNCH_ERROR={exc}", "COMPILE_EXIT=127"])
        output.write_text("\n".join(lines) + "\n", encoding="utf-8")
        print(f"Report: {output}")
        return 127

    lines.extend(["===== COMPILE STDOUT =====", build.stdout,
                  "===== COMPILE STDERR =====", build.stderr,
                  f"COMPILE_EXIT={build.returncode}", ""])
    if build.returncode != 0:
        lines.append("RUNTIME=SKIPPED_BUILD_FAILED")
        output.write_text("\n".join(lines) + "\n", encoding="utf-8")
        print(f"Report: {output}")
        return build.returncode

    try:
        run = subprocess.run([str(binary)], text=True, capture_output=True, check=False)
    except OSError as exc:
        lines.extend([f"RUNTIME_LAUNCH_ERROR={exc}", "RUNTIME_EXIT=127"])
        output.write_text("\n".join(lines) + "\n", encoding="utf-8")
        print(f"Report: {output}")
        return 127

    lines.extend(["===== RUNTIME STDOUT =====", run.stdout,
                  "===== RUNTIME STDERR =====", run.stderr,
                  f"RUNTIME_EXIT={run.returncode}", "===== END ====="])
    output.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"REPORT_CREATED={output}")
    print(f"COMPILE_EXIT={build.returncode}")
    print(f"RUNTIME_EXIT={run.returncode}")
    return run.returncode


if __name__ == "__main__":
    raise SystemExit(main())
