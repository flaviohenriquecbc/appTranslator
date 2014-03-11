function loginWithFacebook(){
	
	document.body.style.cursor='wait';
	
    FB.login(function(response) {
       if (response.authResponse) {
    	   $.get('/UsuarioController/loginFacebook?token='+response.authResponse.accessToken,{}, function(data){
    		   if(data != null || data.trim() != ""){
    		      conectado(data);    		      
    		   }else{
    		   		document.body.style.cursor='default';
    		   		alert(i18n('erro.conectarFacebook'));
    		   }
    			   
    		});
       } else {
       	   document.body.style.cursor='default';
    	   alert(i18n('erro.conectarFacebookCancelado'));
       }
     }, {scope: 'email,publish_stream'});
}

function getFacebookPermission(){
    FB.login(function(response) {
       if (response.authResponse) {
    	   $.get('/UsuarioController/loginFacebook?token='+response.authResponse.accessToken,{}, function(data){    	   
    		   if(data != null || data.trim() != "")
    			   window.location.reload();
    		   else
    			   alert(i18n('erro.conectarFacebook'));
    		});
       } else {
    	   alert(i18n('erro.conectarFacebookCancelado'));
       }
     }, {scope: 'email,publish_stream'});
}


function conectado(data){		
		window.localStorage.setItem("usuarioNome", data[0]);
		window.localStorage.setItem("usuarioFoto", data[2]);
		window.localStorage.setItem("usuarioId", data[1]);
		window.localStorage.setItem("isLogadoUsuario","true");
		
		//var isRoot = window.location.pathname == "/";
		//if(isRoot)
		//	window.location.href = "../../home";
		//else
		//window.location.reload();
		
		window.location.href = "../../UsuarioController/cadastro";	
}

function desconectar(){
	  $.get('/UsuarioController/disconnectUser',{}, function(data){
		  killSessao();		 
		  window.location = 'Application/index';
	  });
}

function killSessao(){
	 window.localStorage.clear();
	 window.sessionStorage.clear();
}

//Popup
function toggle(div_id) {
	var el = document.getElementById(div_id);
	if ( el.style.display == 'none' ) {	el.style.display = 'block';}
	else {el.style.display = 'none'; $("#tituloPopup").text(i18n('login.header'));}
}

function blanket_size(popUpDivVar) {
	if (typeof window.innerWidth != 'undefined') {
		viewportheight = window.innerHeight;
	} else {
		viewportheight = document.documentElement.clientHeight;
	}
	if ((viewportheight > document.body.parentNode.scrollHeight) && (viewportheight > document.body.parentNode.clientHeight)) {
		blanket_height = viewportheight;
	} else {
		if (document.body.parentNode.clientHeight > document.body.parentNode.scrollHeight) {
			blanket_height = document.body.parentNode.clientHeight;
		} else {
			blanket_height = document.body.parentNode.scrollHeight;
		}
	}
	var blanket = document.getElementById('blanket');
	blanket.style.height = blanket_height + 'px';
}

function window_pos(popUpDivVar) {
	if (typeof window.innerWidth != 'undefined') {
		viewportwidth = window.innerHeight;
	} else {
		viewportwidth = document.documentElement.clientHeight;
	}
	if ((viewportwidth > document.body.parentNode.scrollWidth) && (viewportwidth > document.body.parentNode.clientWidth)) {
		window_width = viewportwidth;
	} else {
		if (document.body.parentNode.clientWidth > document.body.parentNode.scrollWidth) {
			window_width = document.body.parentNode.clientWidth;
		} else {
			window_width = document.body.parentNode.scrollWidth;
		}
	}
	var popUpDiv = document.getElementById(popUpDivVar);
	window_width=window_width/2-200;//200 is half popup's width	
}

function popup(windowname) {
	blanket_size(windowname);
	window_pos(windowname);
	toggle('blanket');
	toggle(windowname);
}

function translateFrase(idFile, idTraducao){
		
	if(confirm('Confirm translation?')){
		var traducao = $("#traducao").val();
		var fraseATraduzir = $("#fraseOriginal").text();
		
		$.ajax({
	        url:  '../../TraducaoController/addTranslation',
	        data: {
	        	"idFile" : idFile, 
	        	"idFrase": idTraducao, 
	        	"fraseTraducao": traducao
	        },
	        success: function(data)
	        {   
	        	var msgVazia = $("#frasesTraduzidasVazia");	        	
	        	
	        	if(msgVazia.is(":visible")){
	        		msgVazia.hide();
	        	}	        		       
	        		        	
	        	$("#listaTraduzida").prepend("<img src=\"/public/images/flavioMini.jpg\" style=\"border-radius: 99px; margin-left:10px;\"><label style=\"width:55px; text-align:right\">asked: </label> <label class=\"labelNormal\">"+fraseATraduzir+"</label><br><label>Your translation: </label> <label class=\"text-muted labelNormal\">"+traducao+"</label><br><br>");
	        	
	        	$("#fraseOriginal").text("");
	        	$("#idiomaTo").text("");	        
	        	$("#traducao").val("");
	        	
	        	$("#coins").text(Number($("#coins").text()) + 1);
	        	
	        	getRandomPhrase();	        
	        },
	        error: function(jqXHR, exception){	              
	        	alert("Error while trying to save translation. Please, try again!");	      
	        } 
	    });
	}
}

function getRandomPhrase(){	
	$.ajax({
	        url:  '../../TraducaoController/getRandomPhraseJSON',
	        data: {},
	        success: function(data)
	        {   	        
	        
				if(data != null && data.id != null){
					$("#fraseOriginal").text(data.original);
	        		$("#idiomaTo").text(data.idiomaTo);
	        		$("#translateBtn").attr("onclick", "translateFrase('"+data.keyFile.raw.id+"','"+data.id+"','"+data.original+"')"); 	        		
				}else{
					$("#notTranslate").show();
					$("#toTranslate").hide();
				}	            
	        },
	        error: function(jqXHR, exception){	              
	        	alert("Error while trying to save translation. Please, try again!");	      
	        } 
	    });
	
}