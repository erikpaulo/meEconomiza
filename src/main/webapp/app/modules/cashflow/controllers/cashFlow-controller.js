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
            });

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

            $scope.incomeAndExpenses = {
                chart: {
                    type: 'spline',
                    plotBackgroundColor: '#FAFAFA',
                    backgroundColor: '#FAFAFA'
//                      width: 850
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
//        pointPlacement: -0.2
                },
                {
                    name: 'Despesas',
                    type: 'column',
//                    yAxis: 1,
                    data: [],
                    color: Constants.GENERAL.GRAPH_COLORS[4],
        pointPadding: 0.4,
//        pointPlacement: -0.2
                },
                {
                    name: 'Média Gastos',
                    type: 'spline',
//                    yAxis: 1,
                    data: [],
//                    color: $scope.productColors[2]
                },
                {
                    name: 'Variação Gastos',
                    type: 'spline',
                    yAxis: 1,
                    data: [],
//                    color: $scope.productColors[3]
                }
                ],
                credits: {enabled: false},
                loading: false
            }
        }
	]);
});

