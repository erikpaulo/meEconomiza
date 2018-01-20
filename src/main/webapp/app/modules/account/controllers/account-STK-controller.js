define(['./module'
        ,'../services/account-resources'
        ,'../services/account-entry-resources'
        ,'../services/index-resources'
        ,'../../shared/services/utils-service'
        ,'../../shared/services/constants'], function (app) {

	app.controller('AccountSTKController', ['$scope', '$interval', '$filter', '$mdDialog', 'AccountResource', 'AccountEntryResource', 'IndexResource', 'Utils', 'Constants',
        function($scope, $interval, $filter, $mdDialog, Account, AccountEntry, Index, Utils, Constants) {
            $scope.appContext.contextMenu.addAction(
                {icon: 'add', tooltip: 'Comprar Ação', onClick: function() {
                    $scope.register();
                }}
            );

            $scope.$watch('account', function(newValue, oldValue){
                updateBalance();
            });

            //10 seconds delay
            $interval(getAccountDetail, 600000);

            $scope.register = function(){
                $mdDialog.show({
                    controller: DialogStockController,
                    templateUrl: 'modules/account/views/new-account-STK-entry-template.html',
                    parent: angular.element(document.body),
//                    locals: {
//                    },
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

            $scope.remove = function(entry){
                new AccountEntry(entry).$remove(function(){
                    new Account($scope.account).$getDetailed(function(data){
                        $scope.root.account = data;

                        addSuccess($scope);
                    });
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
//                    $scope.totalProfit = {
//                        grossValue: 0.0,
//                        grossPercent: 0.0,
//                        netValue: 0.0,
//                        netPercent: 0.0,
//
//                        amountInvested: 0.0
//                    }
                    $scope.activeStocks = {};
                    for (var i in $scope.account.stocks){
//                        $scope.totalProfit.grossValue += $scope.account.stocks[i].grossProfitability;
//                        $scope.totalProfit.amountInvested += $scope.account.stocks[i].amount;
//                        $scope.totalProfit.netValue += $scope.account.stocks[i].netProfitability;

                        if ($scope.account.stocks[i].operation == 'PURCHASE' && $scope.account.stocks[i].quantity > 0){
                            if ($scope.activeStocks[$scope.account.stocks[i].code] == null){
                                $scope.activeStocks[$scope.account.stocks[i].code] = {stock: $scope.account.stocks[i],
                                                                                      qtd: 0,
                                                                                      ai: 0.0,
                                                                                      gp: 0.0,
                                                                                      pgp: 0.0,
                                                                                      lp: 0.0,
                                                                                      date: null}
                            }
                            $scope.activeStocks[$scope.account.stocks[i].code].qtd += $scope.account.stocks[i].quantity
                            $scope.activeStocks[$scope.account.stocks[i].code].ai += $scope.account.stocks[i].amount
                            $scope.activeStocks[$scope.account.stocks[i].code].gp += $scope.account.stocks[i].grossProfitability
                            $scope.activeStocks[$scope.account.stocks[i].code].lp += $scope.account.stocks[i].lastPrice
                            $scope.activeStocks[$scope.account.stocks[i].code].pgp = $scope.activeStocks[$scope.account.stocks[i].code].gp / $scope.activeStocks[$scope.account.stocks[i].code].ai*100
                            $scope.activeStocks[$scope.account.stocks[i].code].date += $scope.account.stocks[i].date
                        }
                    }

//                    $scope.totalProfit.grossPercent += $scope.totalProfit.grossValue / $scope.totalProfit.amountInvested;
//                    $scope.totalProfit.netPercent   += $scope.totalProfit.netValue / $scope.totalProfit.amountInvested;
                }
            }

            function getAccountDetail(){
                new Account($scope.account).$getDetailed(function(data){
                    $scope.root.account = data;

                    updateBalance();
//                    addSuccess($scope);
                });
            }


            function DialogStockController($scope, $mdDialog, Constants, Utils) {
                $scope.operations = Constants.ACCOUNT_ENTRY.STOCK_OPERATION;

                $scope.updateTotal = function(){
                    if ($scope.editEntry.quantity && $scope.editEntry.originalPrice && $scope.editEntry.brokerage){
                        $scope.editEntry.amount = ($scope.editEntry.quantity * Utils.currencyToNumber($scope.editEntry.originalPrice)) + Utils.currencyToNumber($scope.editEntry.brokerage);
                    }
                }

                $scope.hide = function() {
                    $mdDialog.hide();
                };
                $scope.cancel = function() {
                    $mdDialog.cancel();
                };
                $scope.submit = function() {
                    $scope.editEntry.amount = Utils.currencyToNumber($scope.editEntry.amount);
                    $scope.editEntry.originalPrice = Utils.currencyToNumber($scope.editEntry.originalPrice);
                    $scope.editEntry.brokerage = Utils.currencyToNumber($scope.editEntry.brokerage);
                    $mdDialog.hide($scope.editEntry);
                };
            }

        }
	]);
});

