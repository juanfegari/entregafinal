function loadTrivia() {
    document.getElementById('question').textContent = '¿Cuál es la capital de Francia?';
}

function loadOrder() {
    document.getElementById('orderInfo').textContent = 'Pedido #12345 - Estado: En proceso';
}

function runUSQL() {
    const query = document.getElementById('query').value;
    document.getElementById('result').textContent = 'Resultado de la consulta: ' + query;
}