
var nextMsg=0;
var Mensagens;

function refreshWall() {

	var wall=document.getElementById("newWallName").value;




  request.onload = function() {



    PrintMSG(this.responseText,nextMsg);

    nextMsg=nextMsg+1; 
    setTimeout(getWall, 100);

  };

  request.onerror = function() { 
    PrintMsgError();

  };

  request.ontimeout = function() { 
    PrintMsgError();

  };


  

  var request = new XMLHttpRequest();
  request.open("GET", "/walls/" + wall , true);
  request.send();
}







function getWall() {

  deleteWallMessages();


  var request = new XMLHttpRequest();

  var wall=document.getElementById("newWallName").value;
  document.getElementById("wallname").innerHTML = wall;



  request.onload = function() {


    Mensagens=this.responseText;



    divideMessages();


  };

  request.onerror = function() { 
    PrintMsgError();
    alert("entrou error");
  };

  request.ontimeout = function() { 
    PrintMsgError();
    alert("entrou timeout");
  };


  
  request.open("GET", "/walls/" + wall , true);
  request.send();

}

function addMSG() {

  var request = new XMLHttpRequest();

  var wall=document.getElementById("newWallName").value;
  var message= document.getElementById("wallMessage").value;


  request.onload = function() {



    deleteWallMessages();
    getWall();


    


  };

  request.onerror = function() { 
    PrintMsgError();

  };

  request.ontimeout = function() { 
    PrintMsgError();
    alert("entrou timeout");
  };


  
  request.open("POST", "/walls/" + wall + "/" + message , true);
  request.send();

}

function deleteMessage(messageNumber){




  var request = new XMLHttpRequest();

  var wall=document.getElementById("newWallName").value;

  request.onload = function() {



    


  };

  request.onerror = function() { 
    PrintMsgError();

  };

  request.ontimeout = function() { 
    PrintMsgError();

  };


  
  request.open("DELETE", "/walls/" + wall + "/" + messageNumber , true);
  request.send();


}





function deleteWall(){

    //Adicionar o AJAX - limpa vidros

    deleteWallMessages();

    var request = new XMLHttpRequest();

    var wall=document.getElementById("newWallName").value;

    request.onload = function() {






    };

    request.onerror = function() { 
      PrintMsgError();

    };

    request.ontimeout = function() { 
      PrintMsgError();

    };



    request.open("DELETE", "/walls/" + wall , true);
    request.send();



  }





  


  
  function divideMessages(){
    var res = Mensagens.split("%%");

    for(var i = 0; i < res.length; i++){
      
      if (res[i]!=""){
        PrintMSG(res[i],i);
      }
      


    }


  }


  function PrintMSG(texto,msgNum) {
    texto = texto.replace(/%20/g, " ");
    document.getElementById('teste').insertAdjacentHTML('beforeend', "<div class='alert alert-info'><a href='#'' class='close' data-dismiss='alert' onclick='deleteMSG("+ msgNum + ")' aria-label='close'>&times;</a><strong>" + texto + "</div>");
  }

  function PrintMsgError(){
    document.getElementById('teste').insertAdjacentHTML('beforeend', "<div class='alert alert-danger'><a href='#'' class='close' data-dismiss='alert' aria-label='close'>&times;</a><strong> ERRO DE CONECÇÃO - VERIFIQUE O SERVIDOR</div>");
  }

  function deleteMSG(msgNum) {
    deleteMessage(msgNum);
  }

  function deleteWallMessages(){
    var myNode = document.getElementById("teste");
    myNode.innerHTML = '';
  }


