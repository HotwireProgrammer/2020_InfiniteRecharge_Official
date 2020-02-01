<script>
import { NetworkTables } from '../utils/networktables'
import zingchartVue from 'zingchart-vue';

export default {
    name: 'MotorChart',

    components: {zingchartVue},
    mounted:function(){
     //   NetworkTables.addKeyListener('/SmartDashboard/' + this.networkKey, this.updateIndicator);

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
                    "max-items":3,
                    overflow:"page",

                },

                series: [{
                    values: [4,5,3,3,4,9],
                    text : "Test"
                },{
                    values: [5,9,4,5,3,3,4,9],
                    text : "Test2"
                }]
            }
        }
    ),

    methods: {
    }
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
