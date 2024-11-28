import os
import json
from flask import Flask, request, jsonify
import requests

# Variables para la API de Hugging Face
API_URLS = {
    "default": "https://o3vpmc8fnn018pev.us-east-1.aws.endpoints.huggingface.cloud",
    "model_2": "https://wclv70ueyrhpgoy1.us-east-1.aws.endpoints.huggingface.cloud",
}
HEADERS = {
    "Accept": "application/json",
    "Authorization": "Bearer <value>", # Sustituir <value> por token de HuggingFace con permisos de escritura y lectura
    "Content-Type": "application/json"
}

app = Flask(__name__)

def getLLamaresponse(input_text, no_words, api_url):
    payload = {
        "inputs": f"{input_text}",
        "parameters": {
            "max_new_tokens": int(no_words) if no_words else 150,
            "temperature": 0.01,
        }
    }

    response = requests.post(api_url, headers=HEADERS, json=payload)
    if response.status_code == 200:
        json_response = response.json()
        if isinstance(json_response, list) and 'generated_text' in json_response[0]:
            return json_response[0]['generated_text']
        else:
            return "Error: No se pudo encontrar el campo 'generated_text' en la respuesta."
    else:
        return f"Error: {response.status_code}, {response.text}"


@app.route('/', methods=['GET'])
def status():
    return jsonify({'response': "UP"})


@app.route('/llm/generate_text', methods=['POST'])
def generate_blogs_default():
    data = request.get_json()
    input_text = data.get('input_text')
    no_words = data.get('no_words')
    
    response = getLLamaresponse(input_text, no_words, API_URLS['default'])
    return jsonify({'response': response})

@app.route('/llm/generate_text/alternative', methods=['POST'])
def generate_blogs_alternative():
    data = request.get_json()
    input_text = data.get('input_text')
    no_words = data.get('no_words')

    response = getLLamaresponse(input_text, no_words, API_URLS['model_2'])
    return jsonify({'response': response})


if __name__ == '__main__':
    port = int(os.environ.get("PORT", 8080))
    app.run(host='0.0.0.0', port=port)
