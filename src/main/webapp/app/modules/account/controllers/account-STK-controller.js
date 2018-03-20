define(['./module'
        ,'../services/account-resources'
        ,'../services/account-entry-resources'
        ,'../services/stock-sale-profit-resources'
        ,'../services/index-resources'
        ,'../../shared/services/utils-service'
        ,'../../shared/services/constants'], function (app) {

	app.controller('AccountSTKController', ['$scope', '$interval', '$filter', '$mdDialog', 'AccountResource', 'AccountEntryResource', 'IndexResource', 'StockSaleProfitResource', 'Utils', 'Constants',
        function($scope, $interval, $filter, $mdDialog, Account, AccountEntry, Index, StockSaleProfit, Utils, Constants) {
            $scope.appContext.contextMenu.addAction(
                {icon: 'add', tooltip: 'Comprar Ação', onClick: function() {
                    $scope.register();
                }}
            );
            $scope.appContext.contextMenu.addAction(
                {icon: 'attach_money', tooltip: 'Pagar IR', onClick: function() {
                    $scope.payIncomeTax();
                }}
            );

            $scope.abs = Utils.abs;

            $scope.$watch('account', function(newValue, oldValue){
                updateBalance();
            });

            //10 seconds delay
            var updatePosition = $interval(getAccountDetail, 600000);

            $scope.$on('$destroy', function() {
                // Make sure that the interval is destroyed too
                $interval.cancel(updatePosition);
            });

            $scope.register = function(){
                $mdDialog.show({
                    controller: DialogStockController,
                    templateUrl: 'modules/account/views/new-account-STK-entry-template.html',
                    parent: angular.element(document.body),
                    clickOutsideToClose:true
                }).then(function(newEntry){
                    if (newEntry){
                        var accountEntry = new AccountEntry(newEntry);
                        accountEntry.type = 'STK';
                        accountEntry.accountId = $scope.account.id;

                        accountEntry.$save(function(e){
                            getAccountDetail()
                        }, function(err){
                            addError($scope, 'Não foi possível atualizar o lançamento.', err);
                        });
                    }
                });
            }

            $scope.payIncomeTax = function(){
                $mdDialog.show({
                    controller: DialogStockSaleProfitController,
                    templateUrl: 'modules/account/views/new-sale-profit-payment-STK-entry-template.html',
                    parent: angular.element(document.body),
                    clickOutsideToClose:true
                }).then(function(payment){
                    var stockSaleProfit = new StockSaleProfit(payment);
                    stockSaleProfit.accountId = $scope.account.id;

                    stockSaleProfit.$save(function(e){
                        getAccountDetail()
                    }, function(err){
                        addError($scope, 'Não foi possível registrar pagamento.', err);
                    });
                });
            }

            $scope.remove = function(entry){
                new AccountEntry(entry).$remove(function(){
                    getAccountDetail();
                }, function(err){
                    addError($scope, 'Não foi possível remover operação.', err);
                });
            }

            $scope.removeIR = function(IRPayment){
                new StockSaleProfit(IRPayment).$remove(function(){
                    getAccountDetail();
                }, function(err){
                    addError($scope, 'Não foi possível remover pagamento.', err);
                });
            }

            $scope.updatePrice = function(entry){
                entry.lastPrice = Utils.currencyToNumber(entry.lastPrice)
                 new AccountEntry(entry).$save(function(){
                     getAccountDetail();
                 });
            }

            function updateBalance(){
                if ($scope.account != null){
                    $scope.account.stocks = $filter('orderBy')($scope.account.stocks, 'date', false);
                    $scope.activeStocks = {};
                    for (var i in $scope.account.stocks){

                        if ($scope.account.stocks[i].operation == 'PURCHASE' && $scope.account.stocks[i].quantity > 0){
                            if ($scope.activeStocks[$scope.account.stocks[i].code] == null){
                                $scope.activeStocks[$scope.account.stocks[i].code] = {stock: $scope.account.stocks[i],
                                                                                      qtd: 0,
                                                                                      ai: 0.0,
                                                                                      cp: 0.0,
                                                                                      gp: 0.0,
                                                                                      pgp: 0.0,
                                                                                      lp: 0.0,
                                                                                      weight:0.0,
                                                                                      date: null}
                            }
                            $scope.activeStocks[$scope.account.stocks[i].code].qtd += $scope.account.stocks[i].quantity
                            $scope.activeStocks[$scope.account.stocks[i].code].ai += $scope.account.stocks[i].amount
                            $scope.activeStocks[$scope.account.stocks[i].code].cp += $scope.account.stocks[i].currentValue
                            $scope.activeStocks[$scope.account.stocks[i].code].gp += $scope.account.stocks[i].grossProfitability
                            $scope.activeStocks[$scope.account.stocks[i].code].lp = $scope.account.stocks[i].lastPrice
                            $scope.activeStocks[$scope.account.stocks[i].code].pgp = $scope.activeStocks[$scope.account.stocks[i].code].gp / $scope.activeStocks[$scope.account.stocks[i].code].ai*100
                            $scope.activeStocks[$scope.account.stocks[i].code].weight = $scope.activeStocks[$scope.account.stocks[i].code].cp / $scope.account.grossBalance * 100
                            $scope.activeStocks[$scope.account.stocks[i].code].date += $scope.account.stocks[i].date
                        }
                    }
                }
            }

            function getAccountDetail(){
                new Account($scope.account).$getDetailed(function(data){
                    $scope.root.account = data;
                });
            }


            function DialogStockController($scope, $mdDialog, Constants, Utils) {
                $scope.operations = Constants.ACCOUNT_ENTRY.STOCK_OPERATION;
                $scope.editEntry = {assets:[{}]}

                $scope.total = 0.0;
                $scope.updateTotal = function(){
                    $scope.total = 0;
//                    $scope.total += ($scope.editEntry.tax != undefined ? Utils.currencyToNumber($scope.editEntry.tax) : 0)
//                    $scope.total += ($scope.editEntry.brokerage != undefined ? Utils.currencyToNumber($scope.editEntry.brokerage) : 0 );

                    angular.forEach($scope.editEntry.assets, function(asset){
                        if (asset.operation != undefined){
                            var signal = -1;

                            if (asset.operation == 'SALE') {
                                signal = 1;
                            }

                            if (asset.quantity != null && asset.originalPrice != null){
                                var brokerageTax = 0.00032157;
                                var brokerTax = 0.87;

                                asset.brokerage = (asset.quantity * Utils.currencyToNumber(asset.originalPrice) * brokerageTax) + (brokerTax)

                                $scope.total += (asset.quantity * Utils.currencyToNumber(asset.originalPrice) * signal) - asset.brokerage;
                            }

                        }
                    });
                }

                $scope.add = function(){
                    $scope.editEntry.assets.push({});
                }

                $scope.hide = function() {
                    $mdDialog.hide();
                };
                $scope.cancel = function() {
                    $mdDialog.cancel();
                };
                $scope.submit = function() {
                    $scope.editEntry.brokerage = Utils.currencyToNumber($scope.editEntry.brokerage);
                    $scope.editEntry.tax = Utils.currencyToNumber($scope.editEntry.tax);

                    angular.forEach($scope.editEntry.assets, function(asset){
                        asset.amount = Utils.currencyToNumber(asset.amount);
                        asset.originalPrice = Utils.currencyToNumber(asset.originalPrice);
                    });
                    $mdDialog.hide($scope.editEntry);
                };
            }


            function DialogStockSaleProfitController($scope, $mdDialog, Constants, Utils) {

                $scope.hide = function() {
                    $mdDialog.hide();
                };
                $scope.cancel = function() {
                    $mdDialog.cancel();
                };
                $scope.submit = function() {
                    $scope.payment.incomeTax = Utils.currencyToNumber($scope.payment.incomeTax);
                    $mdDialog.hide($scope.payment);
                };
            }

        }
	]);
});

