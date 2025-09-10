#!/usr/bin/env bash
set -euo pipefail

OUT="external-issues.json"

# Busca todos los reports de PIT (con o sin timestamp)
mapfile -t REPORTS < <(find . -type f \( -path "*/target/pit-reports/*/mutations.xml" -o -path "*/target/pit-reports/mutations.xml" \) | sort)

python3 - "$OUT" "${REPORTS[@]}" << 'PY'
import json, os, sys, xml.etree.ElementTree as ET

# Uso: python this.py OUT report1 report2 ...
if len(sys.argv) < 2:
    print("Uso incorrecto", file=sys.stderr); sys.exit(2)

out = sys.argv[1]
reports = sys.argv[2:]
issues = []

# Si no hay reports, deja estructura vacía válida
if not reports:
    with open(out, "w", encoding="utf-8") as f:
        f.write('{"issues":[]}')
    print("[INFO] No PIT reports. external-issues.json con lista vacía.", file=sys.stderr)
    sys.exit(0)

repo_root = os.getcwd().replace("\\","/")

def module_dir_from_report(report_path:str)->str:
    # .../module/target/pit-reports[/timestamp]/mutations.xml  -> module dir = ../../..
    d = os.path.dirname(report_path)
    if os.path.basename(d) != "pit-reports":
        d = os.path.dirname(d)
    return os.path.normpath(os.path.join(d, "..", ".."))

def rel_if_exists(*candidates):
    for c in candidates:
        p = c.replace("\\","/")
        if os.path.exists(p):
            return os.path.relpath(p, repo_root).replace("\\","/")
    # fallback: devuelve el primero relativo al repo
    return os.path.relpath(candidates[0], repo_root).replace("\\","/")

for rpt in reports:
    rpt_abs = os.path.abspath(rpt)
    mod_dir = module_dir_from_report(rpt_abs)
    try:
        tree = ET.parse(rpt_abs)
    except Exception as e:
        print(f"[WARN] No se pudo parsear {rpt_abs}: {e}", file=sys.stderr)
        continue
    root = tree.getroot()

    # Recorre mutaciones
    for m in root.findall(".//mutation"):
        status = (m.get("status") or "").upper()
        # Solo reportamos problemas: mutantes vivos y no cubiertos
        if status not in ("SURVIVED","NO_COVERAGE"):
            continue

        # Datos básicos
        try:
            line = int((m.findtext("lineNumber") or "1"))
        except ValueError:
            line = 1
        source_file = m.findtext("sourceFile") or "Unknown.java"
        mutated_class = m.findtext("mutatedClass") or ""
        mutated_method = m.findtext("mutatedMethod") or ""
        mutator = m.findtext("mutator") or "unknown-mutator"
        desc = (m.findtext("description") or "").strip()

        # Resolver ruta del .java/.kt
        pkg_path = mutated_class.replace(".", "/")
        pkg_dir = "/".join(pkg_path.split("/")[:-1]) if "/" in pkg_path else ""
        cand_java = os.path.join(mod_dir, "src/main/java", pkg_dir, source_file)
        cand_kotlin = os.path.join(mod_dir, "src/main/kotlin", pkg_dir, source_file)
        file_rel = rel_if_exists(cand_java, cand_kotlin)

        severity = "MAJOR" if status == "SURVIVED" else "CRITICAL"
        rule_id = (mutator.split(".")[-1] or "mutant")
        message = f"Mutante {status}: {rule_id} en {mutated_class}.{mutated_method}(). {desc}".strip()

        issues.append({
          "engineId": "pit",
          "ruleId": rule_id,
          "severity": severity,
          "type": "BUG",
          "primaryLocation": {
            "message": message,
            "filePath": file_rel,
            "textRange": {"startLine": line, "endLine": line}
          }
        })

with open(out, "w", encoding="utf-8") as f:
    json.dump({"issues": issues}, f, ensure_ascii=False, indent=2)

print(f"[OK] {out} con {len(issues)} issues", file=sys.stderr)
PY

# Garantiza que exista aunque algo falle arriba
if [[ ! -s "$OUT" ]]; then
  echo '{"issues":[]}' > "$OUT"
fi

echo "✅ $OUT listo"
