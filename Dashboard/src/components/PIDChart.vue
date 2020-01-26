<script>
// import VueApexCharts from 'vue-apexcharts'
import { NetworkTables } from '../utils/networktables';
import Trend from "vuetrend"
// import Vue from 'vue';

const SERIES_LENGTH = 1000;
let rpms = [];
let rpmGoal = [];
let startingRpmGoalVal = 76;

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
    // rpms.push(0);
    // rpmGoal.push(startingRpmGoalVal);
}


export default {
    name: 'PIDChart',
    components: {
        // apexchart: VueApexCharts,
        Trend
    },

    mounted: function() {
        this.setRpmGoal(this.rpmGoalVal);
        NetworkTables.addKeyListener('/SmartDashboard/Shooter_RPM', this.addSpeedvalue);
        // setInterval(() => {
        //     this.rpmData.push(addData(this.rpmData));
        //     console.log(this.rpmData.length);
        //     if(this.rpmData.length > 1000) {
        //         this.rpmData.shift();
        //     }
        // }, 10);
        // setInterval(() => {
        //     let data = [this.series[0].data];
        //     data[0].shift();
        //     data[0].push(parseInt(this.rpmGoalVal));

        //     let tmp = this.series[1].data;
        //     tmp.shift();
        //     tmp.push((addData(tmp)));
        //     data.push(tmp);

        //     this.$children[0].updateSeries([{
        //         name: 'RPM Goal',
        //         data: data[0]
        //     },{
        //         name: 'RPMs',
        //         data: data[1]
        //     }]);

        // }, 1000);
    },
    methods: {
        addSpeedvalue: function(key, newSpeed) {
            // console.log('newSpeed', newSpeed);
            let time = new Date();
            this.rpmData.push(newSpeed)
            // let data = [this.series[0].data];
            // data[0].shift();
            // data[0].push([time, parseInt(this.rpmGoalVal)]);

            // let tmp = this.series[0].data;
            // tmp.shift();
            // tmp.push([time, newSpeed*-1]);
            // data.push(tmp);

            // this.$children[0].appendData([{
            //     data: [[time, newSpeed*-1]]
            // }]);
        },
        setRpmGoal: function(newGoal) {
            this.rpmGoalVal = newGoal;
        },
        send: function() {
            NetworkTables.putValue('/SmartDashboard/Shooter_I', this.iVal);
            NetworkTables.putValue('/SmartDashboard/Shooter_D', this.dVal);
            NetworkTables.putValue('/SmartDashboard/Shooter_P', this.pVal);
            NetworkTables.putValue('/SmartDashboard/Shooter_RPMTarget', this.rpmGoalVal);
            console.log('p', this.pVal);
            console.log('i', this.iVal);
            console.log('d', this.dVal);
        }
    },
    data: function() {
        return {
            pVal: 0,
            iVal: 0,
            dVal: 0,
            rpmGoalVal: startingRpmGoalVal,
            series: [{
                name: 'RPMs',
                data: []
            }],
            rpmData: [],
            chartOptions: {
                chart: {
                    animations: {
                        enabled: false
                    },
                    zoom: {
                        enabled: false
                    },
                    toolbar: {
                        autoSelected: 'zoom'
                    }
                },
                stroke: {
                    show: true,
                    curve: 'smooth',
                    width: [2, 2],
                },

                dataLabels: {
                    enabled: false
                },
                theme: {
                    mode: 'dark',
                    palette: 'palette3'
                },
                title: {
                    text: 'Blaster RPMs',
                    align: 'Center'
                },
                yaxis: {
                    type: 'number',
                    title: {
                        text: 'RPMs'
                    },
                    min: 0,
                    // max: 100
                },
                xaxis: {
                    type: 'datetime',
                    datetimeFormatter: {
                        year: 'yyyy',
                        month: "MMM 'yy",
                        day: 'dd MMM',
                        hour: 'HH:mm',
                        seconds: 'mm:ss:fff'
                    },
                    title: {
                        text: 'Time'
                    },
                    // min: 0,
                    // max: 100
                }
            }
        }
    }
};

</script>

<template>
    <div>
        <!-- <apexchart type="line" :options="chartOptions" :series="series" /> -->
        <trend
            :data="rpmData"
            auto-draw
        >
                    <!-- :gradient="['#6fa8dc', '#42b983', '#2c3e50']"
            smooth -->
        </trend>
        <v-row no-gutters>
            <v-col cols="1">
                <v-text-field label="P" v-model="pVal"></v-text-field>
            </v-col>
            <v-col cols="1">
                <v-text-field label="I" v-model="iVal"></v-text-field>
            </v-col>
            <v-col cols="1">
                <v-text-field label="D" v-model="dVal"></v-text-field>
            </v-col>
             <v-col cols="4">
                <v-text-field label="PRM Goal" v-model="rpmGoalVal" @change="setRpmGoal"></v-text-field>
            </v-col>
             <v-col cols="4">
                <v-btn outlined color="primary" @click="send()">Send</v-btn>
            </v-col>
        </v-row>
    </div>
</template>