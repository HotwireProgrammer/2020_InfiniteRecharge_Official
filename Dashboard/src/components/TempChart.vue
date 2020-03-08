<script>
import { NetworkTables } from '../utils/networktables'
import zingchartVue from 'zingchart-vue';

export default {
    name: 'TempChart',

    components: { zingchartVue },
    mounted: function(){
        this.startChart();
    },
    methods: {
        updateGraph(callback) {
            this.data[0].push(NetworkTables.getValue('/SmartDashboard/DriveTrain_LeftOne', 0));
            this.data[1].push(NetworkTables.getValue('/SmartDashboard/DriveTrain_LeftTwo', 0));
            this.data[2].push(NetworkTables.getValue('/SmartDashboard/DriveTrain_RightOne', 0));
            this.data[3].push(NetworkTables.getValue('/SmartDashboard/DriveTrain_RightTwo', 0));
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
            data: [[], [], [], []],
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
                    text: 'Realtime Tempture',
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
                        padding: 5
                    }
                },
                scaleX: {
                    format: ' ',
                     zooming: true
                },
                scaleY: {
                    zooming: true,
                    label: {
                        text: "Celsius",
                        color: "#ccc"
                    }
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
                        values: this.data[0],
                        text: "Left One",
                        'line-width': 2,

                    },{
                        values: this.data[1],
                        text: "Left Two",
                        'line-width': 2,

                    },{
                        values: this.data[2],
                        text: "Right One",
                        'line-width': 2,

                    },{
                        values: this.data[3],
                        text: "Right Two",
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
