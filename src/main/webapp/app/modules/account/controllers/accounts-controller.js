define(['./module'
        ,'../services/account-resources'
        ,'../../shared/services/utils-service'
        ,'../../shared/services/constants'
        ,'../../categorization/services/subcategory-resources'], function (app) {

	app.controller('AccountsController', ['$scope', '$location', '$mdDialog', 'AccountResource', 'SubCategoryResource', 'Constants', 'Utils',
        function($scope, $location, $mdDialog, Account, SubCategory, Constants, Utils) {
            var actions = [];
            angular.forEach(Constants.ACCOUNT.TYPE, function(type){
                actions.push(
                    {img: type.id+'-icon.png', tooltip: type.name, onClick: function() {
                        openDialog(type.id);
                    }}
                )
            });
            $scope.appContext.contextMenu.setActions(actions);

            // Get all registered accounts;
            Account.listAll(function(accounts){
                $scope.accounts = accounts;
            });

            // Get all enabled institutions
            Account.getInstitutions(function(institutions){
                $scope.institutions = institutions;
            });

            $scope.create = function(){
                openDialog();
            }

            $scope.detail = function(account){
                $location.path('/account/'+ account.type +'/'+ account.id +'/detail');
            }

            function openDialog(type){
                $mdDialog.show({
                    controller: DialogController,
                    templateUrl: 'modules/account/views/new-account-'+ type +'-template.html',
                    parent: angular.element(document.body),
                    locals: {
                        type: type,
                        institutions: $scope.institutions,
                        accounts: $scope.accounts
                    },
                    clickOutsideToClose:true
                }).then(function(newAccount){

                    new Account(newAccount).$save(function(account){
                        $scope.accounts.push(account);
                        addSuccess($scope);
                    }, function(err){
                        addError($scope, 'Não foi possível cadastrar conta.', err);
                    });
                });
            }

            function DialogController($scope, $mdDialog, $filter, Utils, Constants, type, institutions) {
                $scope.newAccount = {type: type};

                $scope.types = Constants.ACCOUNT.TYPE;
                $scope.institutions = institutions;
                // Get all registered accounts;

                $scope.querySearch = function(query){
                    return $filter('filter')($scope.subCategories, query);
                }

                if ($scope.newAccount.type == 'CCA'){
                    SubCategory.listAll(function (subCategories){
                        $scope.subCategories = subCategories;
                    });
                } else if ($scope.newAccount.type == 'INV'){
                    $scope.products = Constants.ACCOUNT.INVESTMENT_PRODUCTS;
                    $scope.liquidityTypes = Constants.ACCOUNT.LIQUIDITY_TYPE;
                    $scope.risks = Constants.ACCOUNT.INVESTMENT_RISK;
                } else if ($scope.newAccount.type == 'STK'){
                    SubCategory.listInvestments(function (subCategories){
                        $scope.subCategories = subCategories;
                    });

                    Account.listAllCKA(function(accounts){
                        $scope.ckaAccounts = accounts;
                    });
                }

                $scope.hide = function() {
                    $mdDialog.cancel();
                };
                $scope.cancel = function() {
                    $mdDialog.cancel();
                };
                $scope.submit = function() {
                    if ($scope.newAccount.startBalance) {
                        $scope.newAccount.startBalance = Utils.currencyToNumber($scope.newAccount.startBalance);
                    }
                    $mdDialog.hide($scope.newAccount);
                };
            }
        }
	]);
});

