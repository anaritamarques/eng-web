/**
 * 
 */

getOpenContracts = function(){
	var user = document.getElementById("userNameSession");
	/*if(user==null){
	alert("You need to login to do this action");
	}
	else{*/
	    //var username = user.innerText;
		var username = "rita";
		var token = '{"username":"'+username+'"}';
		alert(token);
		var tokenfinal = btoa(unescape(encodeURIComponent(token)));
		var xhr = new XMLHttpRequest();
		xhr.open("GET", "http://localhost:8081/engweb/serv/user?op=getopencontracts&token="+tokenfinal, true);
		xhr.onreadystatechange = function() {
			if(this.readyState==4){
				alert("resposta");
		   }
		};
		xhr.send();
	//}
}

