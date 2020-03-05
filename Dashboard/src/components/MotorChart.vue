<script>
import { NetworkTables } from '../utils/networktables'
import zingchartVue from 'zingchart-vue';

export default {
    name: 'MotorChart',

    components: { zingchartVue },
    mounted: function(){
        console.log('mounted', this.ampVals);
        setInterval(() => {
            console.log('interval', this.ampVals);
            this.updateGraph();
        }, 500);
    },
    methods: {
        updateGraph(callback) {
            console.log('updateGraph', this.ampVals);
            // let retval = {};
            for (let i = 0; i < 15; i++) {
                this.ampVals[i].push(NetworkTables.getValue('/SmartDashboard/PDP_' + i, 0));
                // retval['plot'+i] = NetworkTables.getValue('/SmartDashboard/PDP_' + i, 0);
            }
            // callback(JSON.stringify(retval));
        }
    },
    data: function() {
        return {
            ampVals: [[], [], [], [], [], [], [], [], [], [], [], [], [], [], []],
        }
    },
    computed: {
        chartData() {
            return {
                type: 'line',
                globals: {
                    fontFamily: 'Roboto',
                },
                backgroundColor: '#fff',
                title: {
                    backgroundColor: '#1565C0',
                    text: 'Amps Chart',
                    color: '#fff',
                    height: '30x',
                },
                plotarea: {
                    marginTop: '80px'
                },
                crosshairX: {
                    lineWidth: 4,
                    lineStyle: 'dashed',
                    lineColor: '#424242',
                    marker : {
                        visible : true,
                        size : 9
                    },
                    plotLabel: {
                        backgroundColor: '#fff',
                        borderColor: '#e3e3e3',
                        borderRadius:5,
                        padding:15,
                        fontSize: 15,
                        shadow : true,
                        shadowAlpha : 0.2,
                        shadowBlur : 5,
                        shadowDistance : 4,
                    },
                    scaleLabel: {
                        backgroundColor: '#424242',
                        padding:5
                    }
                },
                scaleY: {
                    guide: {
                        visible: false
                    },
                    values: '0:100:25'
                },
                tooltip: {
                    visible: false
                },
                plot: {
                    shadow: 1,
                    shadowColor: '#eee',
                    shadowDistance: '10px',
                    lineWidth:5,
                    hoverState: {visible: false},
                    marker:{ visible: false},
                    aspect:'spline'
                },
                series: [
                    {
                        values: this.ampVals[0],
                        text : "PDP 0"
                    },{
                        values: this.ampVals[1],
                        text : "PDP 1"
                    },{
                        values: this.ampVals[2],
                        text : "PDP 2"
                    },{
                        values: this.ampVals[3],
                        text : "PDP 3"
                    },{
                        values: this.ampVals[4],
                        text : "PDP 4"
                    },{
                        values: this.ampVals[5],
                        text : "PDP 5"
                    },{
                        values: this.ampVals[6],
                        text : "PDP 6"
                    },{
                        values: this.ampVals[7],
                        text : "PDP 7"
                    },{
                        values: this.ampVals[8],
                        text : "PDP 8"
                    },{
                        values: this.ampVals[9],
                        text : "PDP 9"
                    },{
                        values: this.ampVals[10],
                        text : "PDP 10"
                    },{
                        values: this.ampVals[11],
                        text : "PDP 11"
                    },{
                        values: this.ampVals[12],
                        text : "PDP 12"
                    },{
                        values: this.ampVals[12],
                        text : "PDP 13"
                    },{
                        values: this.ampVals[13],
                        text : "PDP 14"
                    }
                ]
            }
        }
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
