<script>
import { NetworkTables } from "../utils/networktables";
import * as logger from "../utils/logger";
import Trend from "vuetrend";

const SERIES_LENGTH = 400;

export default {
  name: "PIDChart",
  components: {
    Trend
  },

  mounted: function() {
    NetworkTables.putValue("/SmartDashboard/Shooter_RPMTarget", 0);
    NetworkTables.addKeyListener(
      "/SmartDashboard/Shooter_RPM",
      this.addRPMvalue
    );
     NetworkTables.addKeyListener(
      "/SmartDashboard/Shooter_Speed",
      this.setSpeedvalue
    );
    this.pVal = NetworkTables.getValue("/SmartDashboard/Shooter_P", 0);
    this.iVal = NetworkTables.getValue("/SmartDashboard/Shooter_I", 0);
    this.dVal = NetworkTables.getValue("/SmartDashboard/Shooter_D", 0);
  },
  methods: {
    addRPMvalue: function(key, newSpeed) {
      this.rpmData.push(newSpeed);
      this.rpmGoalData.push(parseInt(this.rpmGoalVal));

      if (this.rpmData.length > SERIES_LENGTH) {
        this.rpmData.shift();
        this.rpmGoalData.shift();
      }
      this.isInTolerance = this.toleranceCalc() 
    },

    setSpeedvalue: function(key, newSpeed) {
        this.speedvalue = newSpeed;
    },

    toleranceCalc: function() {
        let percent = this.rpmGoalVal / this.rpmData[this.rpmData.length - 1] -1
        return Math.abs(percent) <= this.tolerance;
    },

    send: function() {
      NetworkTables.putValue("/SmartDashboard/Shooter_P", this.pVal);
      NetworkTables.putValue("/SmartDashboard/Shooter_I", this.iVal);
      NetworkTables.putValue("/SmartDashboard/Shooter_D", this.dVal);
      NetworkTables.putValue(
        "/SmartDashboard/Shooter_RPMTarget",
        this.rpmGoalVal
      );
    }
  },
  data: function() {
    return {
      pVal: 0,
      iVal: 0,
      dVal: 0,
      rpmGoalVal: 0,
      rpmData: [],
      rpmGoalData: [],
      speedvalue: 0,
      tolerance: .1,
      isInTolerance: false 

    };
  }
};
</script>

<template>
  <div>
    <trend :data="rpmData" auto-draw :gradient="['#ff3d00']"></trend>
    <v-row>
      <v-col cols="4">
          Speed: {{Math.floor(speedvalue * 100)}} %
      </v-col>
      <v-col cols="4">
          RPMs: <span :class="isInTolerance ? 'acceptable' :'' ">{{Math.floor(rpmData[rpmData.length - 1])}}</span>
      </v-col>
    </v-row>
    <v-row no-gutters>
      <v-col cols="2">
        <v-text-field label="P" v-model="pVal"></v-text-field>
      </v-col>
      <v-col cols="2">
        <v-text-field label="I" v-model="iVal"></v-text-field>
      </v-col>
      <v-col cols="2">
        <v-text-field label="D" v-model="dVal"></v-text-field>
      </v-col>
      <v-col cols="2">
        <v-text-field label="PRM Goal" v-model="rpmGoalVal"></v-text-field>
      </v-col>
      <v-col cols="2">
        <v-text-field label="Tolerance" v-model="tolerance"></v-text-field>
      </v-col>
      <v-col cols="2">
        <v-btn outlined color="primary" @click="send()">Send</v-btn>
      </v-col>
    </v-row>
  </div>
</template>

<style scoped lang="scss">
    .rpm-goal{
        margin-top:-25%;
    }
    .acceptable{
        color: green;
    }
</style>
