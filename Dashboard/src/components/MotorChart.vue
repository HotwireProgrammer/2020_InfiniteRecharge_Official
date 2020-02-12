<script>
import { NetworkTables } from '../utils/networktables'
import zingchartVue from 'zingchart-vue';
let intermVolta = [];


export default {
    name: 'MotorChart',

    components: { zingchartVue },
    mounted:function(){
    //    NetworkTables.addKeyListener('/SmartDashboard/volta', this.updateIndicator);
        setInterval(() => intermVolta.push(3), 300);
    },
    methods: {
        addIntermVolta(key, value) {
            intermVolta.push()
        },
        updateGraph(callback) {
            console.log('called');
            // let retval = [];
            let retval = {
                    plot0: intermVolta[i],
                    plot1: intermVolta[i]
                };
            intermVolta.forEach((val, i) => {
                retval.push({
                    plot0: intermVolta[i],
                    plot1: intermVolta[i]
                });
            });
            callback(JSON.stringify(retval));
        }
    },
    data: () => (
        {
            chartData: {
                type: 'line',

                legend:{ 
                        header:{
                        text:"Header"
                    },
                    footer:{
                        text:"Footer"
                    },
                    overflow:"page",
                },
                refresh: {
                    type: 'feed',
                    transport: 'js',
                    url: 'updateGraph()',
                    interval: 500,
                    maxTicks: 20
                },

                series: [
                    {
                        values: [],
                        text : "Test"
                    },{
                        values: [5,9,4,5,3,3,4,9],
                        text : "Test2"
                    }
                ]
            }
        }
    )
};
</script>

<template>
  <div>
    <zingchartVue class="chart" :data="chartData" />
  </div>
</template>

<style scoped lang="scss">
.chart {
  width: 400px;
}
</style>
