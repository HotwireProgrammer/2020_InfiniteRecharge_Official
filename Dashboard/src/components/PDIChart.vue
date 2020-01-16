<script>
import VueApexCharts from 'vue-apexcharts'
// import Vue from 'vue';

const SERIES_LENGTH = 120;
// let time = 1484418600000;
let volta = [];
let rpmGoal = [];
let startingRpmGoalVal = 76;

function addData() {
    let val = Math.floor((Math.random() * 5) + 40);
    const spikeChance = Math.random();
    if (spikeChance > .98) {
        val += 30;
    } else if (spikeChance > .1 && volta && volta[volta.length-1] && volta[volta.length-1][1] > 60) {
        val += 30;
    }
    return val;
}

for (let i = 0; i < SERIES_LENGTH; i++) {
    // time = time + 1000;
    volta.push(addData())
    rpmGoal.push(startingRpmGoalVal)
}


export default {
    name: 'PDIChart',
    components: {
        apexchart: VueApexCharts,
    },

    mounted: function() {
        this.setRpmGoal(this.rpmGoalVal);
        setInterval(() => {
            // let tmp0 = this.series[0].data;
            // tmp0.shift();
            // this.series[1].data.shift();
            // tmp0.push((addData()));
            // this.series[1].data.push(parseInt(this.rpmGoalVal));
            // this.series[0].data = tmp0;
            // this.series[1].data = this.series[1].data;
            // this.$children[0].resetSeries();
            // this.$children[0].updateSeries([{
            //     name: 'A Volts',
            //     data: this.series[0].data
            // },{
            //     name: 'RPM Goal',
            //     data: this.series[1].data
            // }])

        }, 1000);
    },
    methods: {
        setRpmGoal: function(newGoal) {
            console.log('newGoal', newGoal);
            this.$children[0].updateSeries([
                this.series[0],
                {
                    name: 'RPM Goal',
                    data: Array(SERIES_LENGTH).fill(parseInt(newGoal))
                }
            ])
        },
        send: function() {
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
                name: 'A Volts',
                data: volta
            },{
                name: 'RPM Goal',
                data: Array(SERIES_LENGTH).fill(startingRpmGoalVal)
            }],
            chartOptions: {
                chart: {
                    // animations: {
                    //     enabled: false
                    // },
                    // stacked: false,
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
                // colors: ['ff65fc'],
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
    }
};

</script>

<template>
    <div>
        <apexchart type="line" :options="chartOptions" :series="series" />
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