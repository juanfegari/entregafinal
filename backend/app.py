from flask import Flask, jsonify
from Trivia.trivia import leer_preguntas, generar_preguntas

app = Flask(__name__)

# Ruta para obtener preguntas
@app.route('/api/trivia', methods=['GET'])
def obtener_pregunta():
    preguntas = leer_preguntas('preguntas.csv')  # Asegúrate de tener este archivo CSV
    pregunta = next(generar_preguntas(preguntas, 1))
    return jsonify({
        "question": pregunta[0],
        "options": pregunta[1],
        "correct_answer": pregunta[2]
    })

if __name__ == '__main__':
    app.run(debug=True)