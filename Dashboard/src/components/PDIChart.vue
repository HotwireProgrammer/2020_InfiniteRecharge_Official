<script>
import VueApexCharts from 'vue-apexcharts'
// import Vue from 'vue';

const SERIES_LENGTH = 100;
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
    rpms.push(addData(rpms));
    rpmGoal.push(startingRpmGoalVal);
}


export default {
    name: 'PDIChart',
    components: {
        apexchart: VueApexCharts,
    },

    mounted: function() {
        this.setRpmGoal(this.rpmGoalVal);
        setInterval(() => {
            let data = [this.series[0].data];
            data[0].shift();
            data[0].push(parseInt(this.rpmGoalVal));

            let tmp = this.series[1].data;
            tmp.shift();
            tmp.push((addData(tmp)));
            data.push(tmp);

            this.$children[0].updateSeries([{
                name: 'RPM Goal',
                data: data[0]
            },{
                name: 'RPMs',
                data: data[1]
            }]);

        }, 1000);
    },
    methods: {
        setRpmGoal: function(newGoal) {
            this.rpmGoalVal = newGoal;
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
                name: 'RPM Goal',
                data: Array(SERIES_LENGTH).fill(startingRpmGoalVal)
            },{
                name: 'RPMs',
                data: rpms
            }],
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