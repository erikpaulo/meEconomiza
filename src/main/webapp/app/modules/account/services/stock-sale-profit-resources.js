define(['./module'], 
function (module) {
	
	module.factory('StockSaleProfitResource', function($resource) {
	    return $resource(
	        'api/account/STK/:accountId/saleProfit/:id',
	        {id: '@id', accountId: '@accountId'},
	        {

	        }
	    );
	});
});