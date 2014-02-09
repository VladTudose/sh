	
	

$(document).ready(function() {

	//alert("in");
	 $("#addRQ").click(function() {
		//alert("inA");
        var intId = $("#listResourceQuantity div").length + 1;
        
		var fieldWrapper = $("<div class=\"fieldwrapper\" id=\"field" + intId + "\"/>");
        
		var fName1 =$("<input type=\"text\" name=\"resource\" placeholder=\"Resource\" class=\"duo\"/><br>");
		var fName2 =$("<input type=\"text\" name=\"quantity\" placeholder=\"Quantity\" class=\"duo\"><br>");
		
		
		var removeButton = $("<input type=\"button\" class=\"removeBtnRQ\" value=\"Remove\" />");
        removeButton.click(function() {
            
			
			var parent = $(this).parent(); 
			var itemHeight = parent.height();
			
			var val = $(".form-bg-3").height();
			val -= itemHeight;
			$(".form-bg-3").height(val);
			
			val = $(".form-bg-3 form").height();
			val -= itemHeight;
			$(".form-bg-3 form").height(val);
			
			val = parseInt($("#listResourceQuantity").css("top"), 10);
			val = val+itemHeight;
			$("#listResourceQuantity").css("top",val+"px");
				
			
			
			$(this).parent().remove();
        });
        fieldWrapper.append(fName1);
		fieldWrapper.append(fName2);
		
      
        fieldWrapper.append(removeButton);
		
        $("#listResourceQuantity").append(fieldWrapper);
		var itemHeight = $("#field"+intId).height();
		
		var val = $(".form-bg-3").height();
		val += itemHeight;
		$(".form-bg-3").height(val);
		
		val = $(".form-bg-3 form").height();
		val += itemHeight;
		$(".form-bg-3 form").height(val);
		
		val = parseInt($("#listResourceQuantity").css("top"), 10);
		val = val-itemHeight;
		$("#listResourceQuantity").css("top",val+"px");
    });
	//alert("out");
});