define(['./module'
        ,'../services/account-resources'
        ,'../../shared/services/utils-service'
        ,'../../shared/services/constants'
        ,'./account-CKA-controller'
        ,'./account-INV-controller'
        ,'./account-STK-controller'], function (app) {

	app.controller('AccountController', ['$scope', '$location', '$mdDialog', '$routeParams', 'AccountResource', 'Constants', 'Utils',
        function($scope, $location, $mdDialog, $routeParams, Account, Constants, Utils,) {
            $scope.root = $scope;
            $scope.appContext.contextMenu.setActions([
                {icon: 'close', tooltip: 'Inativar Conta', onClick: function() {
                    $scope.inactivate();
                }},
                {icon: 'edit', tooltip: 'Editar Conta', onClick: function() {
                    $scope.editAccount();
                }}
            ]);

            $scope.entriesPage = 'modules/account/views/account-'+ $routeParams.type +'.html'
            new Account({id: $routeParams.accountID, type: $routeParams.type}).$getDetailed(function(data){
                $scope.account = data;
            });

            $scope.inactivate = function(){
                new Account({id: $scope.account.id}).$delete(function(){
                    $location.path('/accounts');
                })
            }

            $scope.editAccount = function(){
                 $mdDialog.show({
                        controller: DialogController,
                        templateUrl: 'modules/account/views/new-account-'+ $scope.account.type +'-template.html',
                        parent: angular.element(document.body),
                        locals: {
                            account: $scope.account
                        },
                        clickOutsideToClose:true
                }).then(function(newAccount){

                    new Account(newAccount).$save(function(account){
                        $scope.account = account;
                        addSuccess($scope);
                    }, function(err){
                        addError($scope, 'Não foi possível cadastrar conta.', err);
                    });
                });
            }


            function DialogController($scope, $mdDialog, Utils, account) {
                $scope.newAccount = angular.copy(account);

                // Get all enabled institutions
                Account.getInstitutions(function(institutions){
                    $scope.institutions = institutions;
                });

                if ($scope.newAccount.type == 'INV'){
                    $scope.products = Constants.ACCOUNT.INVESTMENT_PRODUCTS;
                    $scope.liquidityTypes = Constants.ACCOUNT.LIQUIDITY_TYPE;
                    $scope.risks = Constants.ACCOUNT.INVESTMENT_RISK;
                }

                $scope.hide = function() {
                    $mdDialog.cancel();
                };
                $scope.cancel = function() {
                    $mdDialog.cancel();
                };
                $scope.submit = function() {
                    $scope.newAccount.startBalance = Utils.currencyToNumber($scope.newAccount.startBalance);
                    $mdDialog.hide($scope.newAccount);
                };
            }
        }
	]);
});

