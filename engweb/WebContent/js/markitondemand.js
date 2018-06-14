/** 
 * Version 1.0, Jan 2012
 */

var Markit = {};
/**
* Define the QuoteService.
* First argument is symbol (string) for the quote. Examples: AAPL, MSFT, JNJ, GOOG.
* Second argument is fCallback, a callback function executed onSuccess of API.
*/
Markit.QuoteService = function(sSymbol, fCallback) {
    this.symbol = sSymbol;
    this.fCallback = fCallback;
    this.DATA_SRC = "http://dev.markitondemand.com/Api/v2/Quote/jsonp";
    this.makeRequest();
};
/**
* Ajax success callback. fCallback is the 2nd argument in the QuoteService constructor.
*/
Markit.QuoteService.prototype.handleSuccess = function(jsonResult) {
    this.fCallback(jsonResult);
};
/**
* Ajax error callback
*/
Markit.QuoteService.prototype.handleError = function(jsonResult) {
    console.error(jsonResult);
};
/** 
* Starts a new ajax request to the Quote API
*/
Markit.QuoteService.prototype.makeRequest = function() {
    //Abort any open requests
    if (this.xhr) { this.xhr.abort(); }
    //Start a new request
    this.xhr = $.ajax({
        data: { symbol: this.symbol },
        url: this.DATA_SRC,
        dataType: "jsonp",
        success: this.handleSuccess,
        error: this.handleError,
        context: this
    });
};


var getAsset = function(asset){
	  new Markit.QuoteService(asset, function(jsonResult) {

	    //Catch errors
	    if (!jsonResult || jsonResult.Message){
	        console.error("Error: ", jsonResult.Message);
	        return;
	    }

	    //If all goes well, your quote will be here.
	    console.log(jsonResult);
	    
	    var token = '{"symbol":'+jsonResult.Name+',"change":'+jsonResult.Change+'}';
		var tokenfinal = btoa(unescape(encodeURIComponent(token)));
		var xhttp = new XMLHttpRequest();
		xhttp.open("POST", "http://localhost:8081/engweb/serv/asset?op=newasset&token="+tokenfinal, true);
		xhttp.send()
		
		/*var plafond = document.getElementById("available");
		var xhttp = new XMLHttpRequest();
		xhttp.open("GET", "http://localhost:8081/engweb/serv/user?op=getplafond&token="+tokenfinal, true);
		xhttp.onreadystatechange = function() {
			if(this.readyState==4){
		    	if(this.responseText!="erro" && plafond!=null){
		    		plafond.innerText=responseText;
		    		alert(plafond.innerText);
		    	}
		   }
		};*/
		xhttp.send();

	    document.getElementById("name").innerHTML += "<p id=\"assetName\">"+jsonResult.Name+"</p>";
	    document.getElementById("change").innerHTML += "<p id=\"assetChange\">"+Math.round(jsonResult.Change * 100 )/100+"</p>";
	    document.getElementById("sell").innerHTML += "<table><tr><td><button class='fa fa-scribd' id=\"sell-button\"></button></td><td><p style='margin-left: 8px;' id=\"assetsell\">"+ jsonResult.Low+"</p></td></tr></table>";
	    document.getElementById("buy").innerHTML += "<table><tr><td><button class='fa fa-bold' id=\"buy-button\"></button></td><td><p style='margin-left: 8px;' id=\"assetbuy\">"+ jsonResult.High+"</p></td></tr></table>";


	    var assetName = document.getElementById("assetName").innerHTML;
	    var assetChange = document.getElementById("assetChange").innerHTML;
	    var assetSell = document.getElementById("assetsell").innerHTML;
	    var assetBuy = document.getElementById("assetbuy").innerHTML;
	    
	    // Get the modal
	    var sellmodal = document.getElementById('sell-modal');
	    var buymodal = document.getElementById('buy-modal');

	    // Get the buttons that opens the modal
	    var sellbtn = document.getElementById("sell-button");
	    var buybtn = document.getElementById("buy-button");

	    // Get the <span> element that closes the modal
	    var span = document.getElementsByClassName("close")[0];
	    
	    // When the user clicks the button, open the modal 
	    sellbtn.onclick = function() {
	        sellmodal.style.display = "block";
	        document.getElementById("sell-modal").style.display = "block";

	        document.getElementById("sell-tab").innerHTML = "<br><h4>SELL "+assetName+"</h4>";
	        document.getElementById("sell-tab").innerHTML +="<h4>Sell Price: "+assetSell+"$</h4>";
	        document.getElementById("sell-tab").innerHTML +="<h5>Change: "+assetChange+"%</h5><br>";
	        document.getElementById("sell-tab").innerHTML +="<b id=\"amountTitle\" style='display: inline;'>AMOUNT</b> <input id='amountsell' type='number' name='number' value='1000' min='100' max='10000' step='100' style='display: inline;'/> <button id='to-units' onclick=\"toUnits()\" style='display: inline;'>UNITS</button>";
	        document.getElementById("sell-tab").innerHTML +="<b id=\"unitsTitle\" style='display: none;'>UNITS</b> <input id='unitssell' type='number' name='number' value='10' min='0.01' max='100' step='1' style='display: none;'/> <button id='to-amount' onclick=\"toAmount()\" style='display: none;'>AMOUNT</button>";
	        document.getElementById("sell-tab").innerHTML +="<button id=\"open-sell\" onclick=\"openSellContract()\" style='display: block;' >Open Contract</button>";
	    }
	    
	    buybtn.onclick = function() {
	        buymodal.style.display = "block";        
	        document.getElementById("buy-modal").style.display = "block";
	        
	        document.getElementById("buy-tab").innerHTML = "<br><h4>BUY "+assetName+"</h4>";
	        document.getElementById("buy-tab").innerHTML +="<h4>Buy Price "+assetBuy+"$</h4>";
	        document.getElementById("buy-tab").innerHTML +="<h5>Change "+assetChange+"$</h5><br>";
	        document.getElementById("buy-tab").innerHTML +="<b id=\"amountTitleBuy\" style='display: inline;'>AMOUNT</b> <input id='amountbuy' type='number' name='number' value='1000' min='100' max='10000' step='100' style='display: inline;'/> <button id='to-units-buy' onclick=\"toUnitsBuy()\" style='display: inline;'>UNITS</button>";
	        document.getElementById("buy-tab").innerHTML +="<b id=\"unitsTitleBuy\" style='display: none;'>UNITS</b> <input id='unitsbuy' type='number' name='number' value='10' min='0.01' max='100' step='1' style='display: none;'/> <button id='to-amount-buy' onclick=\"toAmountBuy()\" style='display: none;'>AMOUNT</button>";
	        document.getElementById("buy-tab").innerHTML +="<button id=\"open-buy\" onclick=\"openBuyContract()\" style='display: block';>Open Contract</button>";
	    }
	    
	    // When the user clicks on <span> (x), close the modal
	    span.onclick = function() {
	        sellmodal.style.display = "none";
	        buymodal.style.display = "none";
	    }

	    // When the user clicks anywhere outside of the modal, close it
	    window.onclick = function(event) {
	        if (event.target == sellmodal) {
	            sellmodal.style.display = "none";
	        }
	        if (event.target == buymodal) {
	            buymodal.style.display = "none";
	        }
	    }

	    /*amount.onchange = function(){
	        //alterar o nÂº de unidades (acho que terÃ¡ de ser quando tivermos backend)
	    }
	    **
	    * Need help? Visit the API documentation at:
	    * http://dev.markitondemand.com
	    */
	});
	}

function openSellContract(){
	var user = document.getElementById("userNameSession");
	var amountInput = document.getElementById("amountsell");
	var unitsInput = document.getElementById("unitssell");
	var openingprice = document.getElementById("assetsell").innerHTML;
	var mode="";
	if(amountInput.style.display == "inline"){
		mode = "amount";
	}
	else{
		mode = "units";
	}
	if(mode=="amount"){
		var amount = document.getElementById("amountsell").value;
		var units = Math.round((amount/openingprice)*100)/100;
	}
	else{
		var units = document.getElementById("unitssell").value;
		var amount = units*openingprice;
	}
	/*if(user==null){
		alert("You need to login to do this action");
	}
	else{*/
		//var username = user.innerText;
		var username = "rita";
		var assetName = document.getElementById("assetName").innerHTML;
		var type = 0;
		if(document.getElementById("amountsell")==null) {
			alert("Select the amount you want to invest.");
			return;
		}
		else{
			var token = '{"username":"'+username+'","assetname":"'+assetName+'", "type":'+type+', "invested":'+amount+', "units":'+units+', "openingprice":'+openingprice+'}';
			var tokenfinal = btoa(unescape(encodeURIComponent(token)));

			var xhttp = new XMLHttpRequest();
			xhttp.open("POST", "http://localhost:8081/engweb/serv/assets?op=createcontract&token="+tokenfinal, true);
			xhttp.onreadystatechange = function() {
				if(this.readyState==4){
					document.getElementById('sell-modal').style.display = "none";
			    	switch(this.responseText){
				    	case "sucesso":
				    		alert("Trade is open!");
				    		break;
				     	case "erro":
				     	 	alert("An error occurred");
				    		break;
				    	case "Not enough plafond!":
				    		alert("Not enough plafond!")
				    		break;
				        default:
				        	break;
			    	}
			   }
			};
			xhttp.send();
		}
	//}
	var refreshPlafond = myTimer();
}

function openBuyContract(){
	var user = document.getElementById("userNameSession");
	var amountInput = document.getElementById("amountbuy");
	var unitsInput = document.getElementById("unitsbuy");
	var openingprice = document.getElementById("assetbuy").innerHTML;
	var mode="";
	if(amountInput.style.display == "inline"){
		mode = "amount";
	}
	else{
		mode = "units";
	}
	if(mode==amount){
		var amount = amountInput.value;
		var units = amount/openingprice;
	}
	else{
		var units = unitsInput.value;
		var amount = units*openingprice;
	}
	/*if(user==null){
		alert("You need to login to do this action");
	}
	else{*/
		//var username = user.innerText;
		var username = "rita";
		var assetName = document.getElementById("assetName").innerHTML;
		var type = 0;
		if(document.getElementById("amountbuy")==null) {
			alert("Select the amount you want to invest.");
			return;
		}
		else{
			var token = '{"username":"'+username+'","assetname":"'+assetName+'", "type":'+type+', "invested":'+amount+', "units":'+units+', "openingprice":'+openingprice+'}';
			var tokenfinal = btoa(unescape(encodeURIComponent(token)));

			var xhttp = new XMLHttpRequest();
			xhttp.open("POST", "http://localhost:8081/engweb/serv/assets?op=createcontract&token="+tokenfinal, true);
			xhttp.onreadystatechange = function() {
				if(this.readyState==4){
					document.getElementById('buy-modal').style.display = "none";
			    	switch(this.responseText){
				    	case "sucesso":
				    		alert("Trade is open!");
				    		break;
				     	case "erro":
				     	 	alert("An error occurred");
				    		break;
				    	case "Not enough plafond!":
				    		alert("Not enough plafond!")
				    		break;
				        default:
				        	break;
			    	}
			   }
			};
			xhttp.send();
		}
	//}
	var refreshPlafond = myTimer();
}

function toUnits(){
	var amountTitle = document.getElementById("amountTitle");
	var inputAmount = document.getElementById("amountsell");
	var buttonToUnits = document.getElementById("to-units");
	var unitsTitle = document.getElementById("unitsTitle");
	var inputUnits = document.getElementById("unitssell");
	var buttonToAmount = document.getElementById("to-amount");

	amountTitle.style.display = 'none';
	inputAmount.style.display = 'none';
	buttonToUnits.style.display = 'none';
	unitsTitle.style.display = 'inline';
	inputUnits.style.display = 'inline';
	buttonToAmount.style.display = 'inline';
}

function toAmount(){
	var amountTitle = document.getElementById("amountTitle");
	var inputAmount = document.getElementById("amountsell");
	var buttonToUnits = document.getElementById("to-units");
	var unitsTitle = document.getElementById("unitsTitle");
	var inputUnits = document.getElementById("unitssell");
	var buttonToAmount = document.getElementById("to-amount");

	amountTitle.style.display = 'inline';
	inputAmount.style.display = 'inline';
	buttonToUnits.style.display = 'inline';
	unitsTitle.style.display = 'none';
	inputUnits.style.display = 'none';
	buttonToAmount.style.display = 'none';
}

function toUnitsBuy(){
	var amountTitle = document.getElementById("amountTitleBuy");
	var inputAmount = document.getElementById("amountbuy");
	var buttonToUnits = document.getElementById("to-units-buy");
	var unitsTitle = document.getElementById("unitsTitleBuy");
	var inputUnits = document.getElementById("unitsbuy");
	var buttonToAmount = document.getElementById("to-amount-buy");

	amountTitle.style.display = 'none';
	inputAmount.style.display = 'none';
	buttonToUnits.style.display = 'none';
	unitsTitle.style.display = 'inline';
	inputUnits.style.display = 'inline';
	buttonToAmount.style.display = 'inline';
}

function toAmountBuy(){
	var amountTitle = document.getElementById("amountTitleBuy");
	var inputAmount = document.getElementById("amountbuy");
	var buttonToUnits = document.getElementById("to-units-buy");
	var unitsTitle = document.getElementById("unitsTitleBuy");
	var inputUnits = document.getElementById("unitsbuy");
	var buttonToAmount = document.getElementById("to-amount-buy");

	amountTitle.style.display = 'inline';
	inputAmount.style.display = 'inline';
	buttonToUnits.style.display = 'inline';
	unitsTitle.style.display = 'none';
	inputUnits.style.display = 'none';
	buttonToAmount.style.display = 'none';
}

//Este timer vai atualizar a base de dados de x em x segundos, assim como obter o novo valor de plafond do utilizador
var myVar = setInterval(myTimer, 2000);
function myTimer() {
    var token = '{"name":'+jsonResult.Name+', "symbol":'+jsonResult.Symbol+', "change":'+jsonResult.Change+'"lastPrice":'+jsonResult.LastPrice+
    '"low":'+jsonResult.Low+'"high":'+jsonResult.High+'"changePercent":'+jsonResult.ChangePercent+'}';
	var tokenfinal = btoa(unescape(encodeURIComponent(token)));
	var xhttp = new XMLHttpRequest();
	xhttp.open("POST", "http://localhost:8081/engweb/serv/asset?op=newasset&token="+tokenfinal, true);
	xhttp.send()
	
	var plafond = document.getElementById("available");
	var xhttp = new XMLHttpRequest();
	xhttp.open("GET", "http://localhost:8081/engweb/serv/user?op=getplafond&token="+tokenfinal, true);
	xhttp.onreadystatechange = function() {
		if(this.readyState==4){
	    	if(this.responseText!="erro" && plafond!=null){
	    		plafond.innerText=responseText;
	    		alert(plafond.innerText);
	    	}
	   }
	};
	xhttp.send();
	
	var invested = document.getElementById("invested");
	var xhttp = new XMLHttpRequest();
	xhttp.open("GET", "http://localhost:8081/engweb/serv/user?op=getinvested&token="+tokenfinal, true);
	xhttp.onreadystatechange = function() {
		if(this.readyState==4){
	    	if(this.responseText!="erro" && invested!=null){
	    		invested.innerText=responseText;
	    	}
	   }
	};
	xhttp.send();
	
	var profit = document.getElementById("available");
	var xhttp = new XMLHttpRequest();
	xhttp.open("GET", "http://localhost:8081/engweb/serv/user?op=getprofit&token="+tokenfinal, true);
	xhttp.onreadystatechange = function() {
		if(this.readyState==4){
	    	if(this.responseText!="erro" && profit!=null){
	    		profit.innerText=responseText;
	    		var total = plafond.innerText+invested.innerText+profit.innerText;
	    		var equity = document.getElementById("equity");
	    		equity.innerText = total;
	    	}
	   }
	};
	xhttp.send();
	
	var total = plafond.innerText+invested.innerText+profit.innerText;
	
}