# update_api.py
import os
import re

def replace_in_file(file_path, old_pattern, new_pattern):
    with open(file_path, 'r', encoding='utf-8') as file:
        content = file.read()
    
    new_content = re.sub(old_pattern, new_pattern, content)
    
    if content != new_content:
        with open(file_path, 'w', encoding='utf-8') as file:
            file.write(new_content)
        print(f"✓ {file_path} güncellendi")
        return True
    return False

def update_extractor_links(directory):
    pattern = r'ExtractorLink\('
    replacement = 'newExtractorLink('
    
    updated_files = 0
    
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.kt'):
                file_path = os.path.join(root, file)
                if replace_in_file(file_path, pattern, replacement):
                    updated_files += 1
    
    print(f"Toplam {updated_files} dosya güncellendi")

if __name__ == "__main__":
    kotlin_dir = "src"  # Kotlin dosyalarının bulunduğu dizin
    update_extractor_links(kotlin_dir)