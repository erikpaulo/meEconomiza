define(['./module'
        ,'../../shared/services/utils-service'
        ,'../../shared/services/constants'
        ,'modules/cashflow/services/cashFlow-resources'], function (app) {

	app.controller('CashFlowController', ['$scope', '$filter', 'CashFlowResource', 'Constants',
        function($scope, $filter, CashFlow, Constants) {
            $scope.appContext.contextMenu.setActions([]);

            $scope.consolidatedCF = null;
            CashFlow.consolidate(function(data){
                $scope.consolidatedCF = data;

                updateIncomeExpenses();
                updateSubCategoryType();
            });

            function updateSubCategoryType(){
                var months = []
                var date = new Date($scope.consolidatedCF.year, 0, 01);

                $scope.supCategoryType.series[0].data = [];
                $scope.supCategoryType.series[1].data = [];
                for(var i=0;i<12;i++){
                    if ($scope.consolidatedCF.perMonthExpSuperfluous[i]!=0 || $scope.consolidatedCF.perMonthExpEssential[i]!=0){
                        $scope.supCategoryType.series[0].data.push($scope.consolidatedCF.perMonthExpSuperfluous[i] * -1)
                        $scope.supCategoryType.series[1].data.push($scope.consolidatedCF.perMonthExpEssential[i] * -1)

                        date.setMonth(i);
                        $scope.supCategoryType.xAxis.categories.push($filter('date')(date, "MMM/yy", 'UTC').toUpperCase())
                    }
                }
            }

            function updateIncomeExpenses(){
                var months = []
                var date = new Date($scope.consolidatedCF.year, 0, 01);

                $scope.incomeAndExpenses.series[0].data = [];
                $scope.incomeAndExpenses.series[1].data = [];
                $scope.incomeAndExpenses.series[2].data = [];
                $scope.incomeAndExpenses.series[3].data = [];
                for(var i=0;i<12;i++){
                    if ($scope.consolidatedCF.perMonthIncome[i]!=0 || $scope.consolidatedCF.perMonthExpense[i]!=0){
                        $scope.incomeAndExpenses.series[0].data.push($scope.consolidatedCF.perMonthIncome[i])
                        $scope.incomeAndExpenses.series[1].data.push(Math.abs($scope.consolidatedCF.perMonthExpense[i]))
                        $scope.incomeAndExpenses.series[2].data.push(Math.abs($scope.consolidatedCF.perMonthExpAverage[i]))
                        $scope.incomeAndExpenses.series[3].data.push($scope.consolidatedCF.perMonthExpVariation[i])

                        date.setMonth(i);
                        $scope.incomeAndExpenses.xAxis.categories.push($filter('date')(date, "MMM/yy", 'UTC').toUpperCase())
                    }
                }
            }

            $scope.supCategoryType = {
                chart: {
                    type: 'column',
                    plotBackgroundColor: '#FAFAFA',
                    backgroundColor: '#FAFAFA'
                },
                title: {
                    text: 'Despesas por Importância'
                },
                xAxis: {
                    categories: []
                },
                yAxis: {
                    min: 0,
                    title: {
                        text: 'Despesa Total'
                    },
                    stackLabels: {
                        enabled: false,
                        style: {
                            fontWeight: 'bold',
                            color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                        }
                    }
                },
                tooltip: {
                    headerFormat: '<b>{point.x}</b><br/>',
                    pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
                },
                plotOptions: {
                    column: {
                        stacking: 'normal',
                        dataLabels: {
                            enabled: true,
                            formatter: function () {
                                return $filter('currency')(this.y)
                            }
                        }
                    }
                },
                series: [{
                    name: 'Supérfluo',
                    data: []
                }, {
                    name: 'Essencial',
                    data: []
                }],
                credits: {enabled: false},
                loading: false
            }

            $scope.incomeAndExpenses = {
                chart: {
                    type: 'spline',
                    plotBackgroundColor: '#FAFAFA',
                    backgroundColor: '#FAFAFA'
                },
                title: {
                    text: 'Entradas e Saídas'
                },
                xAxis: {
                    categories: []
                },
                yAxis: [{ // Primary yAxis
                    labels: {
                        format: '{value}'
                    },
                    title: {
                        text: 'Valor (R$)'
                    }
                }, { // Secondary yAxis
                title: {
                    text: 'Variação Mês Anterior (%)'
                },
                labels: {
                    format: '{value}%'
                },
                opposite: true
                }],
                tooltip: {
                    formatter: function () {
                        return '<b>' + this.x + '</b><br/>' +
                                this.series.name + ': ' +
                                (this.series.name == 'Variação Gastos' ? $filter('number')(this.y, 2) + '%' : $filter('currency')(this.y)) + '<br/>'
                    }
                },
                plotOptions: {
                    column: {
                        grouping: false,
                        shadow: false,
                        borderWidth: 0,
                        dataLabels: {
                            enabled: true,
                            formatter: function () {
                                return $filter('currency')(this.y)
                            }
                        }
                    }
                },
                series: [ {
                    name: 'Ganhos',
                    type: 'column',
                    data: [],
                    color: Constants.GENERAL.GRAPH_COLORS[1],
                    pointPadding: 0.3,
                },
                {
                    name: 'Despesas',
                    type: 'column',
                    data: [],
                    color: Constants.GENERAL.GRAPH_COLORS[4],
                    pointPadding: 0.4,
                },
                {
                    name: 'Média Gastos',
                    type: 'spline',
                    data: [],
                },
                {
                    name: 'Variação Gastos',
                    type: 'spline',
                    yAxis: 1,
                    data: [],
                }
                ],
                credits: {enabled: false},
                loading: false
            }
        }
	]);
});

