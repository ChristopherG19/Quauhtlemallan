# import json

# # Function to filter JSONL content based on specific format
# def filter_questions(input_file, output_file):
#     filtered_lines = []
    
#     # Read input JSONL file
#     with open(input_file, 'r', encoding='utf-8') as infile:
#         for line in infile:
#             data = json.loads(line)
#             text = data["text"]
            
#             # Check if the line contains the phrase "¿Cómo se dice"
#             if "¿Cómo se dice" in text:
#                 filtered_lines.append(data)
    
#     # Write filtered content to new JSONL file
#     with open(output_file, 'w', encoding='utf-8') as outfile:
#         for item in filtered_lines:
#             json.dump(item, outfile)
#             outfile.write('\n')

# # Example usage
# input_file = 'temp.jsonl'  # Your input file path
# output_file = 'traducciones_dataset_b.jsonl'  # Your output file path
# filter_questions(input_file, output_file)

# import json
# import re

# # Load the JSONL file and transform it into the required structure
# def transform_to_firebase_structure(input_file, output_file):
#     traducciones = {}

#     # Read input JSONL file using 'utf-8' to handle encoding
#     with open(input_file, 'r', encoding='utf-8') as infile:
#         for line in infile:
#             data = json.loads(line)
#             text = data["text"]
            
#             # Parse the content assuming the structure "Human: <spanish>### Assistant: <maya>"
#             if "Human:" in text and "Assistant:" in text:
#                 try:
#                     # Extract the part after 'Human:' and before '###'
#                     human_part = text.split("Human:")[1].split("###")[0].strip()
#                     assistant_part = text.split("Assistant:")[1].strip()

#                     # Extract the full Spanish phrase before "en"
#                     spanish_word = re.search(r'¿Cómo se dice (.*?) en', human_part).group(1).strip().lower()
                    
#                     # Extract the language
#                     match = re.search(r'en\s([^\s\?]+)\?', text)
#                     if match:
#                         idioma_maya = match.group(1).strip().lower()  # El resultado de la captura se encuentra en el grupo 1
#                         maya_word = assistant_part.split("se dice")[1].strip().rstrip('.').lower()
                        
#                         # Add to the dictionary in the desired structure
#                         if idioma_maya not in traducciones:
#                             traducciones[idioma_maya] = {}
#                         traducciones[idioma_maya][spanish_word] = maya_word
#                     else:
#                         print("No se encontró un idioma maya en el formato esperado")

#                 except (IndexError, AttributeError):
#                     # Skip if the format doesn't match exactly
#                     continue

#     # Write the transformed structure to the output JSON file
#     with open(output_file, 'w', encoding='utf-8') as outfile:
#         json.dump({"traducciones": traducciones}, outfile, ensure_ascii=False, indent=4)

# # Example usage
# input_file = 'traducciones_dataset_b.jsonl'
# output_file = 'traducciones_firebase.json'
# transform_to_firebase_structure(input_file, output_file)

# import json

# # Cargar el archivo JSON
# with open('traducciones_firebase.json', 'r', encoding='utf-8') as file:
#     data = json.load(file)

# # Diccionario que contiene las traducciones
# traducciones = data.get("traducciones", {})

# # Contar cuántos elementos tiene cada idioma
# conteo_por_idioma = {idioma: len(traducciones[idioma]) for idioma in traducciones}

# total = 0

# # Mostrar el resultado
# for idioma, conteo in conteo_por_idioma.items():
#     print(f"Idioma: {idioma}, Número de traducciones: {conteo}")
#     total += conteo
    
# print(f"Conteo total: {total}")
