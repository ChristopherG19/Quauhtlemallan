# import json

# def filter_questions(input_file, output_file):

#     filtered_lines = []
#     with open(input_file, 'r', encoding='utf-8') as infile:
#         for line in infile:
#             data = json.loads(line)
#             text = data["text"]
#             if "¿Cómo se dice" in text:
#                 filtered_lines.append(data)

#     with open(output_file, 'w', encoding='utf-8') as outfile:
#         for item in filtered_lines:
#             json.dump(item, outfile)
#             outfile.write('\n')

# input_file = 'temp.jsonl'
# output_file = 'traducciones_dataset_b.jsonl'
# filter_questions(input_file, output_file)

# import json
# import re

# def transform_to_firebase_structure(input_file, output_file):
#     traducciones = {}

#     with open(input_file, 'r', encoding='utf-8') as infile:
#         for line in infile:
#             data = json.loads(line)
#             text = data["text"]
            
#             if "Human:" in text and "Assistant:" in text:
#                 try:
#                     human_part = text.split("Human:")[1].split("###")[0].strip()
#                     assistant_part = text.split("Assistant:")[1].strip()
#                     spanish_word = re.search(r'¿Cómo se dice (.*?) en', human_part).group(1).strip().lower()
                    
#                     match = re.search(r'en\s([^\s\?]+)\?', text)
#                     if match:
#                         idioma_maya = match.group(1).strip().lower()
#                         maya_word = assistant_part.split("se dice")[1].strip().rstrip('.').lower()

#                         if idioma_maya not in traducciones:
#                             traducciones[idioma_maya] = {}
#                         traducciones[idioma_maya][spanish_word] = maya_word
#                     else:
#                         print("No se encontró un idioma maya en el formato esperado")

#                 except (IndexError, AttributeError):
#                     continue

#     with open(output_file, 'w', encoding='utf-8') as outfile:
#         json.dump({"traducciones": traducciones}, outfile, ensure_ascii=False, indent=4)

# input_file = 'traducciones_dataset_b.jsonl'
# output_file = 'traducciones_firebase.json'
# transform_to_firebase_structure(input_file, output_file)

# import json

# with open('traducciones_firebase.json', 'r', encoding='utf-8') as file:
#     data = json.load(file)

# traducciones = data.get("traducciones", {})
# conteo_por_idioma = {idioma: len(traducciones[idioma]) for idioma in traducciones}

# total = 0

# for idioma, conteo in conteo_por_idioma.items():
#     print(f"Idioma: {idioma}, Número de traducciones: {conteo}")
#     total += conteo
    
# print(f"Conteo total: {total}")
