	
$(document).ready(function() {

		function addToRow(row,value,link){
			var row1 = $('<td onclick="document.location=\'http://localhost:9000/api'+link +'\'"></td>').addClass('bar').text(value);
			row.append(row1);
		}	
		function deleteItem(link){
				var url ="/api"+link;
                var x = new XMLHttpRequest();
                x.open("DELETE", url, true);
                x.send(null);
		}
		function addToDeleteRow(row,item){
			var row1 = $('<td ></td>').addClass('bar');
			row.append(row1);
			var deleteButton = $('<button id=\"'+ item._links.self.href +'\"></button>');
			row1.append(deleteButton);
			$(deleteButton).parent().parent().attr('id',   item._links.self.href);
			$(deleteButton).click(function (event) {
				deleteItem($(this).attr('id'));
				$(this).parent().parent().remove();
			});
		}		
		
		$( document ).ready(function() {
			render();
		});
			
		function render() {
		var table = $('<table id=\"data\"></table>').addClass('foo');
			
			row = $('<tr id=\"header\"></tr>');
			table.append(row);
			addToRow(row,"Quantity");
			addToRow(row,"Description");
			addToRow(row,"Name");
			addToRow(row,"");
				
					
			$.ajax({
                    type: "GET",
                    url: "http://localhost:9000/api/resourcequantities",
                    acccept: "application/json",
                    success: function(response) {
							print(response);
                        }
                    })
                
				
			
			$('#container').append(table);
			
		}
		function print(response){
			for(var i=0;i<response._embedded.resourcequantities.length;i++)
			{
				var item = response._embedded.resourcequantities[i];
				var link = item._links.self.href;
				row = $('<tr ></tr>');
				$("#data").append(row);
				addToRow(row,item.quantity,link);
				addToRow(row,item._embedded.resource.description,link);
				addToRow(row,item._embedded.resource.name,link);
				addToDeleteRow(row,item);
			}
		}
	
	
});