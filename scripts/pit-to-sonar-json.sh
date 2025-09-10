#!/usr/bin/env bash
set -euo pipefail

# Busca todos los reports de PIT en submódulos (con o sin timestampedReports)
mapfile -t REPORTS < <(find . -type f -path "*/target/pit-reports/*/mutations.xml" -o -path "*/target/pit-reports/mutations.xml" | sort)

OUT="external-issues.json"
TMP="$(mktemp)"
python3 - "$OUT" "${REPORTS[@]}" << 'PY'
import json, os, sys, xml.etree.ElementTree as ET

if len(sys.argv) < 3:
    open(sys.argv[1], "w").write("[]")
    sys.exit(0)

out = sys.argv[1]
reports = sys.argv[2:]
issues = []

repo_root = os.getcwd().replace("\\","/")

def module_dir_from_report(report_path:str)->str:
    # .../module/target/pit-reports[/timestamp]/mutations.xml  -> module dir = ../../..
    d = os.path.dirname(report_path)
    # si hay carpeta timestamp, subimos 2 niveles; si no, igual
    if os.path.basename(d) != "pit-reports":
        d = os.path.dirname(d)
    return os.path.normpath(os.path.join(d, "..", ".."))

def rel_if_exists(*candidates):
    for c in candidates:
        p = c.replace("\\","/")
        if os.path.exists(p):
            return os.path.relpath(p, repo_root).replace("\\","/")
    # si no existe, devuelve el primero (mejor algo que nada)
    return os.path.relpath(candidates[0], repo_root).replace("\\","/")

for rpt in reports:
    rpt = os.path.abspath(rpt)
    mod_dir = module_dir_from_report(rpt)
    try:
        tree = ET.parse(rpt)
    except Exception as e:
        print(f"[WARN] No se pudo parsear {rpt}: {e}", file=sys.stderr)
        continue
    root = tree.getroot()

    # PIT: <mutation status="SURVIVED|KILLED|NO_COVERAGE|..."><mutator>...</mutator>...
    for m in root.findall(".//mutation"):
        status = (m.get("status") or "").upper()
        if status not in ("SURVIVED","NO_COVERAGE"):
            continue  # solo reportamos los "vivos" o no cubiertos

        line = int((m.findtext("lineNumber") or "1"))
        source_file = m.findtext("sourceFile") or "Unknown.java"
        mutated_class = m.findtext("mutatedClass") or ""
        mutated_method = m.findtext("mutatedMethod") or ""
        mutator = m.findtext("mutator") or "unknown-mutator"
        desc = m.findtext("description") or ""

        # Ruta fuente: intenta Java y luego Kotlin
        pkg_path = mutated_class.replace(".", "/")
        # Sustituye el nombre de clase por el fichero real (por si inner classes)
        if "/" in pkg_path:
            pkg_dir = "/".join(pkg_path.split("/")[:-1])
        else:
            pkg_dir = ""
        cand_java = os.path.join(mod_dir, "src/main/java", pkg_dir, source_file)
        cand_kotlin = os.path.join(mod_dir, "src/main/kotlin", pkg_dir, source_file)
        file_rel = rel_if_exists(cand_java, cand_kotlin)

        severity = "MAJOR" if status == "SURVIVED" else "CRITICAL"
        rule_id = (mutator.split(".")[-1] or "mutant")

        message = f"Mutante {status}: {rule_id} en {mutated_class}.{mutated_method}(). {desc}".strip()

        issue = {
          "engineId": "pit",
          "ruleId": rule_id,
          "severity": severity,
          "type": "BUG",
          "primaryLocation": {
            "message": message,
            "filePath": file_rel,
            "textRange": {"startLine": line, "endLine": line}
          }
        }
        issues.append(issue)

with open(out, "w", encoding="utf-8") as f:
    json.dump(issues, f, ensure_ascii=False, indent=2)
print(f"[OK] Generado {out} con {len(issues)} issues", file=sys.stderr)
PY

mv "$OUT" "$TMP"
# Asegura un JSON válido aunque no haya reports
if [[ ! -s "$TMP" ]]; then
  echo "[]" > "$OUT"
else
  mv "$TMP" "$OUT"
fi

echo "✅ ${OUT} listo"
