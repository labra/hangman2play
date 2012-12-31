var canvas;
var ctx;
var palabrasOffline = new Array("BIENVENIDO","HOLA","Palabra");
var palabra; 
var aciertos = new Array(); 
var fallos = new Array(); 
var spanPal;
var juego = false;  // Indica si hay juego activo
var ls   = false;   // indica si hay almacenamiento local (localStorage)

$(document).ready(function () {
 
 ls = Modernizr.localstorage;
 palabras = getPalabras();
 

 if (Modernizr.canvas) {
  canvas = document.getElementById("idCanvas");
  ctx = canvas.getContext("2d");
 }
 
 // Crear botones con las letras...
 var divBotones = document.getElementById("botonesLetras");  
 var abecedario = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ";
 for (var i = 0; i < abecedario.length; i++) {
   var letra = abecedario.charAt(i);
   var botLetra = document.createElement("input");
   botLetra.setAttribute("type","button");
   botLetra.setAttribute("class","botLetra");
   botLetra.setAttribute("value",letra);
   botLetra.setAttribute("id",letra);
   botLetra.setAttribute("onclick","encajarLetra('"+letra+"')");
   divBotones.appendChild(botLetra);
 }; 
 
 // Intenta recuperar juego existente
 recuperarJuego();
 
 // Si no hay juego, se crea uno
 if (!juego) nuevoJuego();

 pintarJuego();
}
);

function getPalabras() {
 
 // Si no se pueden obtener de otro sitio, coger las predefinidas...
 return palabrasOffline;
}

function recuperarJuego() {
 if (ls) {
   juego = recuperar('juego');
   if (juego) {
	palabra = recuperar('palabra');
	aciertos = recuperar('aciertos');
	fallos = recuperar('fallos');
   }
 }
}

function guardar(clave,valor) {
 if (ls) {
  window.localStorage.setItem(clave,JSON.stringify(valor));
 }
}

function recuperar(clave) {
 if (ls) {
  return JSON.parse(window.localStorage.getItem(clave));
 }
}

function nuevoJuego() {

  palabra = palabras[Math.floor(Math.random()*palabras.length)].toUpperCase();
  // Limpiar lista de aciertos y de fallos
  aciertos = [];
  fallos = [];

  if (ls) {
   guardar('juego',true);
   guardar('palabra',palabra);
   guardar('aciertos',aciertos);
   guardar('fallos',fallos);
  }
}

function pintarJuego() {
  ponLetras();
  $(".botLetra").css("visibility","visible");
  
  if (canvas) {
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  // Poner fondo en gradiente...
  var grd = ctx.createLinearGradient(0, canvas.height, canvas.width, 0);
  grd.addColorStop(0, '#ceefff');
  grd.addColorStop(1, '#52bcff');
  ctx.fillStyle = grd;
  ctx.fillRect(0, 0, canvas.width, canvas.height);
  
  ctx.strokeStyle = "red" ;
  ctx.fillStyle = "blue" ;
  
  for (var i = 1; i <= fallos.length; i++) { 
   pintarElemento(i);
  }
 }
}

function ponLetras() {

 // TODO: reescribirlo con JQuery...
 
 var letras = document.getElementById('letras');
 // Borra todas las letras para volver a pintarlas
 // Igual es más eficiente cambiar las que correspondan 
 // en vez de borrarlas todas para volver a pintarlas...
 while (letras.hasChildNodes()) {
    letras.removeChild(letras.lastChild);
  } 

 // Añade hijos de tipo span por cada letra de la palabra
 for (var i = 0; i < palabra.length; i++) {
   var caracter = document.createElement("span");
   caracter.setAttribute("class","letra");
   if (!letraEnLista(palabra[i],aciertos))  {
    // Si la letra no está en la lista de aciertos se escribe _
    caracter.textContent = "_\u00A0";
   }
   else {
    caracter.textContent = palabra[i]+"\u00A0";
   }
   letras.appendChild(caracter); 
 }
 
};

function pintarElemento(i){
 if (i > 6) {
  finJuego("Has perdido");
 } 
 else {
  if (canvas) {
   switch (i) {
    case 1: dibujaCara(); break;
    case 2: dibujaCuerpo(); break;
    case 3: dibujaBrazoIzq(); break;
    case 4: dibujaBrazoDer(); break;
    case 5: dibujaPiernaIzq(); break;
    case 6: dibujaPiernaDer(); break;
    case 7: dibujaAhorcado(); break;
   }
  }
 }
}


function dibujaCara() {
 ctx.arc(100,50,25,0,Math.PI*2,true);
 ctx.stroke();
 }

function dibujaCuerpo() {
 ctx.beginPath();
 ctx.moveTo(75,75);
 ctx.lineTo(125,75);
 ctx.lineTo(125,150);
 ctx.lineTo(75,150);
 ctx.lineTo(75,75);
 ctx.closePath();
 ctx.stroke();
}

function dibujaBrazoIzq() {
 ctx.beginPath();
 ctx.moveTo(75,75);
 ctx.lineTo(50,100);
 ctx.lineTo(50,125);
 ctx.lineTo(75,100);
 ctx.lineTo(75,75);
 ctx.closePath();
 ctx.stroke();
}

function dibujaBrazoDer() {
 ctx.beginPath();
 ctx.moveTo(125,75);
 ctx.lineTo(150,100);
 ctx.lineTo(150,125);
 ctx.lineTo(125,100);
 ctx.lineTo(125,75);
 ctx.closePath();
 ctx.stroke();
}

function dibujaPiernaIzq() {
 ctx.beginPath();
 ctx.moveTo(75,150);
 ctx.lineTo(95,150);
 ctx.lineTo(95,200);
 ctx.lineTo(75,200);
 ctx.lineTo(75,150);
 ctx.closePath();
 ctx.stroke();
}

function dibujaPiernaDer() {
 ctx.beginPath();
 ctx.moveTo(105,150);
 ctx.lineTo(125,150);
 ctx.lineTo(125,200);
 ctx.lineTo(105,200);
 ctx.lineTo(105,150);
 ctx.closePath();
 ctx.stroke();
}

function dibujaAhorcado() {
 ctx.fillStyle = "red";
 ctx.beginPath();
 ctx.arc(100,50,25,0,Math.PI*2,true);
 ctx.closePath();
 ctx.stroke();
 ctx.fill();
}

function dibujaAcertado() {
 ctx.fillStyle = "yellow";
 ctx.beginPath();
 
 // Cabeza
 ctx.arc(100,50,25,0,Math.PI*2,true);
 ctx.fill();

 // Boca
 ctx.beginPath();
 ctx.moveTo(100,50);
 ctx.arc(100,50,10,0,Math.PI,false);
 ctx.closePath();
 ctx.stroke();

 // Ojo
 ctx.beginPath();
 ctx.moveTo(93,40); 
 ctx.arc(93,40,2,0,Math.PI*2,true);
 ctx.closePath();
 ctx.stroke();
 
 // Otro ojo
 ctx.beginPath();
 ctx.moveTo(107,40); 
 ctx.arc(107,40,2,0,Math.PI*2,true);
 ctx.closePath();
 ctx.stroke();
}


function encajarLetra(letra) {
 var botonLetra = document.getElementById(letra);
 botonLetra.style.visibility = "hidden";
 if (!letraEnLista(letra,palabra)) {

  // Si la tecla no está en la palabra = fallo
  // Se añade a la lista de fallos si no se había añadido ya
  if (!letraEnLista(letra,fallos)) {
	fallos.push(letra);
    guardar('fallos',fallos);
	ponLetras();
	pintarElemento(fallos.length);
  }
 }
 else {
  // Acierto (si no estaba en la lista de aciertos, se mete)
  if (!letraEnLista(letra,aciertos)) {
   aciertos.push(letra);
   guardar('aciertos',aciertos);
   ponLetras();
   // Comprobar si ya se acertaron todas las letras
   if (todasAcertadas(palabra,aciertos)) {
    dibujaAcertado();
	finJuego("Has ganado");
   }
  }
 }
}

function todasAcertadas(palabra,aciertos) {
 for (var i=0; i<palabra.length; i++) {
  if (!letraEnLista(palabra[i],aciertos)) return false;
 }
 return true;
}

function letraEnLista(letra,palabra) {
 // Antes: return (palabra.indexOf(letra) != -1);
 // Utilizo función de jQuery por compatibilidad con IE
 return (jQuery.inArray(letra,palabra)!= -1);
}

function finJuego(mensaje) {
 jugando = false;
 window.alert(mensaje);
 nuevoJuego(); 
 pintarJuego();
}

