<script>
import VueApexCharts from 'vue-apexcharts'
import { NetworkTables } from '../utils/networktables'
import * as logger from '../utils/logger'

const SERIES_LENGTH = 100;
let voltA = [];
let voltB = [];
let voltC = [];
let voltD = [];
let voltE = [];
// let startDangerLevelData = [];
let startingDangerLevel = 76;

function addData(dataList) {
    let val = Math.floor((Math.random() * 5) + 40);
    const spikeChance = Math.random();
    if (spikeChance > .99) {
        val += 30;
    } else if (spikeChance > .1 && dataList && dataList[dataList.length-1] && dataList[dataList.length-1] > 60) {
        val += 30;
    }
    return val;
}

for (let i = 0; i < SERIES_LENGTH; i++) {
    // time = time + 1000;
    voltA.push(addData(voltA));
    voltB.push(addData(voltB));
    voltC.push(addData(voltC));
    voltD.push(addData(voltD));
    voltE.push(addData(voltE));
    // startDangerLevelData.push(startingDangerLevel);
}

export default {
    name: 'PowerChart',
    components: {
        apexchart: VueApexCharts,
    },
    data: () => {
        return {
            dangerLevel: startingDangerLevel,
            series: [
                {
                    name: 'Danger Level',
                    data: Array(SERIES_LENGTH).fill(startingDangerLevel)
                },{
                    name: 'A Volts',
                    data: voltA
                },{
                    name: 'B Volts',
                    data: voltB
                },{
                    name: 'C Volts',
                    data: voltC
                },{
                    name: 'D Volts',
                    data: voltD
                },{
                    name: 'E Volts',
                    data: voltE
                }
            ],
            chartOptions: {
                chart: {
                    animations: {
                        enabled: false
                    },
                    stacked: false,
                    zoom: {
                        type: 'x',
                        enabled: true,
                        autoScaleYaxis: false
                    },
                    toolbar: {
                        autoSelected: 'zoom'
                    }
                },
                stroke: {
                    width: 2
                },
                dataLabels: {
                    enabled: false
                },
                theme: {
                    mode: 'dark',
                    palette: 'palette3'
                },
                title: {
                    text: 'Motor Stats',
                    align: 'left'
                },
                yaxis: {
                    type: 'number',
                    title: {
                        text: 'Volts'
                    },
                    min: 0,
                    max: 100
                },
                xaxis: {
                    type: 'numeric',
                    title: {
                        text: 'Time'
                    },
                    min: 0,
                    max: 100
                }
            }
        }
    },
    methods: {
        updateVoltA: function (newValue) {
            let d = this.series[0].data
            d.shift();
            d.push();
            this.series = [{
                data: d
            }]
            // this.series[0].data = d;
            // logger.logData('voltA', newValue)
        }
    },
    mounted: function() {
        NetworkTables.addKeyListener('/SmartDashboard/VoltA', this.updateVoltA);
        setInterval(() => {
            let data = [this.series[0].data];
            data[0].shift();
            data[0].push(parseInt(this.dangerLevel));

            for (let i = 1; i < 6; i++) {
                let tmp = this.series[i].data;
                tmp.shift();
                tmp.push((addData(tmp)));
                data.push(tmp);
            }
            // logger.logData('voltA', data[1][data[1].length-1])
            // logger.logData('voltB', data[2][data[2].length-1])
            // logger.logData('voltC', data[3][data[3].length-1])
            // logger.logData('voltD', data[4][data[4].length-1])
            // logger.logData('voltE', data[5][data[5].length-1])

            debugger;

            this.$children[0].updateSeries([{
                name: 'Danger Level',
                data: data[0]
            },{
                name: 'A Volts',
                data: data[1]
            },{
                name: 'B Volts',
                data: data[2]
            },{
                name: 'C Volts',
                data: data[3]
            },{
                name: 'D Volts',
                data: data[4]
            },{
                name: 'E Volts',
                data: data[5]
            },]);
            // this.updateVoltA(addData())
        }, 1000);
    }
};

</script>

<template>
    <div>
        <apexchart type=line width=850 :options="chartOptions" :series="series" />   
        <v-checkbox v-model="showVoltage" label="Voltage"></v-checkbox>
    </div>
</template>