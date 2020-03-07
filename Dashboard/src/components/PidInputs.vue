<script>
import { NetworkTables } from "../utils/networktables";
import * as logger from "../utils/logger";

const SERIES_LENGTH = 400;

export default {
    name: "PidInputs",
    components: {},

    mounted: function() {


        // NetworkTables.putValue("/SmartDashboard/Shooter_RPMTarget", 0);
        // NetworkTables.addKeyListener(
        //     "/SmartDashboard/Shooter_RPM",
        //     this.addRPMvalue
        // );
        // NetworkTables.addKeyListener(
        //     "/SmartDashboard/Shooter_Speed",
        //     this.setSpeedvalue
        // );
        this.pVal = NetworkTables.getValue("/SmartDashboard/"+ this.baseStr +"_P", 0);
        this.iVal = NetworkTables.getValue("/SmartDashboard/"+ this.baseStr +"_I", 0);
        this.dVal = NetworkTables.getValue("/SmartDashboard/"+ this.baseStr +"_D", 0);
    },
    props: {
        baseStr: {
            type: String,
            required: true
        }
    },
    methods: {
        // addRPMvalue: function(key, newSpeed) {
        //     this.rpmData.push(newSpeed);
        //     this.rpmGoalData.push(parseInt(this.rpmGoalVal));

        //     if (this.rpmData.length > SERIES_LENGTH) {
        //         this.rpmData.shift();
        //         this.rpmGoalData.shift();
        //     }
        //     this.isInTolerance = this.toleranceCalc() 
        // },

        // setSpeedvalue: function(key, newSpeed) {
        //     this.speedvalue = newSpeed;
        // },

        // toleranceCalc: function() {
        //     let percent = this.rpmGoalVal / this.rpmData[this.rpmData.length - 1] -1
        //     return Math.abs(percent) <= this.tolerance;
        // },

        send: function() {
            NetworkTables.putValue("/SmartDashboard/"+ this.baseStr +"_P", this.pVal);
            NetworkTables.putValue("/SmartDashboard/"+ this.baseStr +"_I", this.iVal);
            NetworkTables.putValue("/SmartDashboard/"+ this.baseStr +"_D", this.dVal);
            // NetworkTables.putValue(
            //     "/SmartDashboard/Shooter_RPMTarget",
            //     this.rpmGoalVal
            // );
        }
    },
    data: function() {
        return {
            pVal: 0,
            iVal: 0,
            dVal: 0,
        };
    }
};
</script>

<template>
<div>
    <!--
    <trend :data="rpmData" auto-draw :gradient="['#ff3d00']"></trend>
    -->
    <v-row no-gutters>
        <v-col cols="3" class="pid-label">
            {{baseStr}}
        </v-col>
        <v-col cols="1">
            <v-text-field label="P" v-model="pVal"></v-text-field>
        </v-col>
        <v-col cols="1">
            <v-text-field label="I" v-model="iVal"></v-text-field>
        </v-col>
        <v-col cols="1">
            <v-text-field label="D" v-model="dVal"></v-text-field>
        </v-col>
        <!--
        <v-col cols="1">
            <v-text-field label="PRM Goal" v-model="rpmGoalVal"></v-text-field>
        </v-col>
        <v-col cols="1">
            <v-text-field label="Tolerance" v-model="tolerance"></v-text-field>
        </v-col>
        -->
        <v-col cols="1">
            <v-btn outlined color="primary" @click="send()">Send</v-btn>
        </v-col>
    </v-row>
    <!--
    <v-row>
        <v-col cols="2">
            Speed: {{Math.floor(speedvalue * 100)}} %
        </v-col>
        <v-col cols="2">
            RPMs: <span :class="isInTolerance ? 'acceptable' :'' ">{{Math.floor(rpmData[rpmData.length - 1])}}</span>
        </v-col>
    </v-row>
    -->
</div>
</template>

<style scoped lang="scss">
    .pid-label {
        padding-top: 10px;
    }
</style>
