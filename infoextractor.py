#!/usr/bin/env python3
import os
from pathlib import Path

def main():
    # 1) imposta la directory root (cartella dove risiede questo script)
    root: Path = Path(__file__).parent.resolve()
    output_file: Path = root / "allifos.txt"

    # 2) lista di tutti i file da aggregare
    files = []
    for path in root.rglob("*"):
        if not path.is_file():
            continue
        # includi tutti i pom.xml
        if path.name == "pom.xml":
            files.append(path)
            continue
        # includi qualsiasi file dentro una cartella "src"
        # controlliamo se uno degli elementi di path.parts è "src"
        if "src" in path.parts:
            files.append(path)

    # 3) ordina per percorso relativo
    files.sort(key=lambda p: str(p.relative_to(root)))

    # 4) scrivi in allifos.txt (sovrascrive se già esiste)
    with output_file.open("w", encoding="utf-8") as out:
        for f in files:
            rel = f.relative_to(root).as_posix()
            out.write(f"### File: {rel}\n")
            out.write("-" * 40 + "\n")
            # leggi e scrivi contenuto
            try:
                text = f.read_text(encoding="utf-8")
            except Exception as e:
                text = f"<<ERROR reading file: {e}>>\n"
            out.write(text)
            out.write("\n\n")

    print(f"✓ Tutti i pom.xml e i file sotto 'src' sono stati aggregati in {output_file}")

if __name__ == "__main__":
    main()
