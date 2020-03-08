<script>
import { NetworkTables } from '../utils/networktables'
import zingchartVue from 'zingchart-vue';

export default {
    name: 'MotorChart',

    components: { zingchartVue },
    mounted: function(){
        this.startChart();
    },
    methods: {
        updateGraph(callback) {
            for (let i = 0; i < 16; i++) {
                this.ampVals[i].push(NetworkTables.getValue('/SmartDashboard/PDP_' + i, 0));
                if (this.ampVals[i].length > 40) {
                    this.ampVals[i].shift();
                }
            }
        },
        startChart() {
            this.chartIntervalId = setInterval(() => {
                this.updateGraph();
            }, 500);
            this.buttonText = "Pause Data Feed"
        },
        pauseChart() {
            clearInterval(this.chartIntervalId);
            this.chartIntervalId = null;
            this.buttonText = "Start Data Feed"
        },
        toggleChart() {
            if (this.chartIntervalId === null) {
                this.startChart();
            } else {
                this.pauseChart();
            }
        }
    },
    data: function() {
        return {
            ampVals: [[], [], [], [], [], [], [], [], [], [], [], [], [], [], [], []],
            chartIntervalId: null,
            buttonText: "Pause Data Feed"
        }
    },
    computed: {
        chartData() {
            return {
                type: 'line',
                backgroundColor: '#303030',
                title: {
                    backgroundColor: '#404040',
                    text: 'Realtime Amperage',
                    color: '#fff',
                    height: '30px',
                },
                plotarea: {
                    marginTop: '80px'
                },
                crosshairX: {
                    lineWidth: 4,
                    lineStyle: 'dashed',
                    lineColor: '#ff3d00',
                    marker : {
                        visible : true,
                        size : 3
                    },
                    plotLabel: {
                        backgroundColor: '#222',
                        color: '#fff',
                        borderColor: '#ddd',
                        borderRadius: 5,
                        padding: 15,
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
                scaleX: {
                    format: ' ',
                     zooming: true
                },
                scaleY: {
                    zooming: true
                },
                tooltip: {
                    visible: false
                },
                plot: {
                    shadow: 1,
                    shadowColor: '#eee',
                    shadowDistance: '10px',
                    lineWidth: 5,
                    hoverState: {visible: false},
                    marker: { visible: false},
                    aspect: 'spline'
                },
                series: [
                    {
                        values: this.ampVals[0],
                        text: "Right DT (0)",
                        'line-width': 2,

                    },{
                        values: this.ampVals[1],
                        text: "Right DT (1)",
                        'line-width': 2,

                    },{
                        values: this.ampVals[2],
                        text: "Shooter (2)",
                        'line-width': 2,

                    },{
                        values: this.ampVals[3],
                        text: "Shooter (3)",
                        'line-width': 2,

                    },{
                        values: this.ampVals[4],
                        text: "Floor Belt (4)",
                        'line-width': 2,

                    },{
                        values: this.ampVals[5],
                        text: "Intake (5)",
                        'line-width': 2,

                    // },{
                    //     values: this.ampVals[6],
                    //     text: "PDP 6",
                    //     'line-width': 2,

                    // },{
                    //     values: this.ampVals[7],
                    //     text: "PDP 7",
                    //     'line-width': 2,

                    // },{
                    //     values: this.ampVals[8],
                    //     text: "PDP 8",
                    //     'line-width': 2,

                    // },{
                    //     values: this.ampVals[9],
                    //     text: "PDP 9",
                    //     'line-width': 2,

                    },{
                        values: this.ampVals[10],
                        text: "Control Panel (10)",
                        'line-width': 2,

                    },{
                        values: this.ampVals[11],
                        text: "Indexer (11)",
                        'line-width': 2,

                    },{
                        values: this.ampVals[12],
                        text: "Climber (12)",
                        'line-width': 2,

                    },{
                        values: this.ampVals[13],
                        text: "Climber (13)",
                        'line-width': 2,

                    },{
                        values: this.ampVals[14],
                        text: "Left DT (14)",
                        'line-width': 2,

                    },{
                        values: this.ampVals[15],
                        text: "Right DT (15)",
                        'line-width': 2,

                    }
                ]
            }
        }
    }
};
</script>

<template>
  <div class="holder">
    <zingchartVue class="chart" :data="chartData" />
    <v-btn
        class="control-button"
        color="primary"
        height="55"
        outlined
        @click="toggleChart()">
            {{buttonText}}
    </v-btn>
  </div>
</template>

<style scoped lang="scss">
.chart {
  width: 400px;
}
.holder {
    text-align: center;
}
</style>
