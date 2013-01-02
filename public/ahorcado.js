var canvas;
var ctx;
var words; 						// Available words
var alphabet;					// alphabet of a given language
var language; 					// Selected language
var word; 						// Word to guess
var successes = new Array(); 	// Successes
var failures = new Array(); 	// Failures
var divLetters; 
var divAlphabet; 
var spanPal;
var inGame = false;  // Active game
var ls   = false;   // indica si hay almacenamiento local (localStorage)

$(function () {
 
 ls = Modernizr.localstorage;

 language = document.getElementById("language").getAttribute("code"); 
 alphabet = document.getElementById("language").getAttribute("alphabet");  
 store("language",language);
 store("alphabet",alphabet);
 resetGame = document.getElementById("newGame").getAttribute("value");  
 divLetters = document.getElementById('letters');
 divAlphabet = document.getElementById('alphabet');

 if (Modernizr.canvas) {
	  canvas = document.getElementById("idCanvas");
	  ctx = canvas.getContext("2d");
}

 if (resetGame == 'true') newGame();
 getGame();
 if (!inGame) newGame();
}

);

function clearAlphabet(){
  while (divAlphabet.hasChildNodes()) {
		   divAlphabet.removeChild(divAlphabet.lastChild);
  } 
}

function paintAlphabet() {
  clearAlphabet();  

  for (var i = 0; i < alphabet.length; i++) {
	   var letter = alphabet.charAt(i);
	   var letterButton = document.createElement("input");
	   letterButton.setAttribute("type","button");
	   letterButton.setAttribute("class","letterButton");
	   letterButton.setAttribute("value",letter);
	   letterButton.setAttribute("id",letter);
	   letterButton.setAttribute("onclick","matchLetter('"+letter+"')");
	   divAlphabet.appendChild(letterButton);
  }; 
}

function getWords() {
 $.getJSON("/words/"+ language + ".json", function(data) {
	 words = [];
	 var output = "<ul>";
	 for (var i in data.words) {
		      output+="<li>" + data.words[i] + "</li>";
		      words.push(data.words[i]) ;
	}
	output+="</ul>";
	document.getElementById("placeholder").innerHTML=output;

	startGame();
 });
}

function getGame() {
 if (ls) {
   inGame = retrieve('inGame');
   if (inGame) {
	word = retrieve('word');
	words = retrieve('words');
	language = retrieve('language');
	successes = retrieve('successes');
	failures = retrieve('failures');
	alphabet = retrieve('alphabet');
	paintGame();
   } 
 }
}

function store(clave,valor) {
 if (ls) {
  window.localStorage.setItem(clave,JSON.stringify(valor));
 }
}

function retrieve(clave) {
 if (ls) {
  return JSON.parse(window.localStorage.getItem(clave));
 }
}

function newGame() {
  $.getJSON("/words/"+ language + ".json", function(data) {
		 words = [];
		 for (var i in data.words) {
			      words.push(data.words[i]) ;
		 }
		 clearLetters();
		 clearAlphabet();
		  
		 word = words[Math.floor(Math.random()*words.length)].toUpperCase();
		 successes = [];
		 failures = [];
		  
		 $("#words").html(words.toString);
		 $("#word").html(word);
		 $("#languageInfo").html(language);
		 $("#alphabetInfo").html(alphabet);
		 $("#successes").html(successes.toString);
		 $("#failures").html(failures.toString);

		 if (ls) {
		   store('inGame',true);
		   store('word',word);
		   store('language',language);
		   store('words',words);
		   store('successes',successes);
		   store('failures',failures);
		   store('alphabet',alphabet);
		  }
		 paintGame();
	 });
}

function paintGame() {

  $("#words").html(words.toString());
  $("#word").html(word);
  $("#languageInfo").html(language);
  $("#alphabet").html(alphabet);

  paintLetters();
  paintAlphabet();
  $(".letterButton").css("visibility","visible");
  
  if (canvas) {
  clearCanvas();
  for (var i = 1; i <= failures.length; i++) { 
   paintElement(i);
  }
 }
}

function clearCanvas() {
	  ctx.clearRect(0, 0, canvas.width, canvas.height);

	  // Gradient background
	  var grd = ctx.createLinearGradient(0, canvas.height, canvas.width, 0);
	  grd.addColorStop(0, '#ceefff');
	  grd.addColorStop(1, '#52bcff');
	  ctx.fillStyle = grd;
	  ctx.fillRect(0, 0, canvas.width, canvas.height);
	  
	  ctx.strokeStyle = "black" ;
	  ctx.fillStyle = "blue" ;
}

function clearLetters(letters) {
	 
  while (divLetters.hasChildNodes()) {
	    divLetters.removeChild(divLetters.lastChild);
	  } 
}

function paintLetters() {

 clearLetters();

 // Add span children for each letter in the word
 for (var i = 0; i < word.length; i++) {
   var character = document.createElement("span");
   character.setAttribute("class","letter");
   if (!letterInWord(word[i],successes))  {
    // If letter is not in success list, write _
    character.textContent = "_\u00A0";
   }
   else {
    character.textContent = word[i]+"\u00A0";
   }
   divLetters.appendChild(character); 
 }
 
};

function paintElement(i){
 if (i > 6) {
  endGame("You lost");
 } 
 else {
  if (canvas) {
   switch (i) {
    case 1: drawFace(); break;
    case 2: drawBody(); break;
    case 3: drawLeftArm(); break;
    case 4: drawRightArm(); break;
    case 5: drawLeftLeg(); break;
    case 6: drawRightArm(); break;
    case 7: drawHangman(); break;
   }
  }
 }
}


function drawFace() {
 ctx.beginPath();
 ctx.arc(100,50,25,0,Math.PI*2,true);
 ctx.closePath();
 ctx.stroke();
}

function drawBody() {
 ctx.beginPath();
 ctx.moveTo(75,75);
 ctx.lineTo(125,75);
 ctx.lineTo(125,150);
 ctx.lineTo(75,150);
 ctx.lineTo(75,75);
 ctx.closePath();
 ctx.stroke();
}

function drawLeftArm() {
 ctx.beginPath();
 ctx.moveTo(75,75);
 ctx.lineTo(50,100);
 ctx.lineTo(50,125);
 ctx.lineTo(75,100);
 ctx.lineTo(75,75);
 ctx.closePath();
 ctx.stroke();
}

function drawRightArm() {
 ctx.beginPath();
 ctx.moveTo(125,75);
 ctx.lineTo(150,100);
 ctx.lineTo(150,125);
 ctx.lineTo(125,100);
 ctx.lineTo(125,75);
 ctx.closePath();
 ctx.stroke();
}

function drawLeftLeg() {
 ctx.beginPath();
 ctx.moveTo(75,150);
 ctx.lineTo(95,150);
 ctx.lineTo(95,200);
 ctx.lineTo(75,200);
 ctx.lineTo(75,150);
 ctx.closePath();
 ctx.stroke();
}

function drawRightLeg() {
 ctx.beginPath();
 ctx.moveTo(105,150);
 ctx.lineTo(125,150);
 ctx.lineTo(125,200);
 ctx.lineTo(105,200);
 ctx.lineTo(105,150);
 ctx.closePath();
 ctx.stroke();
}

function drawHangman() {
 ctx.fillStyle = "red";
 ctx.beginPath();
 ctx.arc(100,50,25,0,Math.PI*2,true);
 ctx.closePath();
 ctx.stroke();
 ctx.fill();
}

function drawSuccess() {
 ctx.fillStyle = "yellow";
 ctx.beginPath();
 
 // Head
 ctx.arc(100,50,25,0,Math.PI*2,true);
 ctx.fill();

 // Mouth
 ctx.beginPath();
 ctx.moveTo(100,50);
 ctx.arc(100,50,10,0,Math.PI,false);
 ctx.closePath();
 ctx.stroke();

 // One eye
 ctx.beginPath();
 ctx.moveTo(93,40); 
 ctx.arc(93,40,2,0,Math.PI*2,true);
 ctx.closePath();
 ctx.stroke();
 
 // Another eye
 ctx.beginPath();
 ctx.moveTo(107,40); 
 ctx.arc(107,40,2,0,Math.PI*2,true);
 ctx.closePath();
 ctx.stroke();
}


function matchLetter(letter) {
 var letterButton = document.getElementById(letter);
 letterButton.style.visibility = "hidden";
 if (!letterInWord(letter,word)) {

  // If key is not in word = failure 
  // Insert in failures unless it was already there
  if (!letterInWord(letter,failures)) {
	failures.push(letter);
    store('failures',failures);
	paintLetters();
	paintElement(failures.length);
  }
 }
 else {
  // Success (if it was not in the successes, insert it)
  if (!letterInWord(letter,successes)) {
   successes.push(letter);
   store('successes',successes);
   paintLetters();
   if (allGuessed(word,successes)) {
    drawSuccess();
	endGame("You won");
   }
  }
 }
}

function allGuessed(word,successes) {
 for (var i=0; i<word.length; i++) {
  if (!letterInWord(word[i],successes)) return false;
 }
 return true;
}

function letterInWord(letter,word) {
 return (jQuery.inArray(letter,word)!= -1);
}

function endGame(msg) {
 inGame = false;
 window.alert(msg);
 newGame(); 
}
