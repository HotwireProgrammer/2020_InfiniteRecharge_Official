<script>
import { NetworkTables } from '../utils/networktables'

export default {
    name: 'BallCouter',

    components: {},
    mounted:function(){
        NetworkTables.addKeyListener('/SmartDashboard/ballCounter', this.ballUpdate);
        this.ballVal = NetworkTables.getValue('/SmartDashboard/ballCounter', 0)
    },
    
    data: () => (
        {
            ballVal: 0,
        }
    ),

    methods: {
        ballUpdate: function(key, value) {
            this.ballVal = value;
        },

        validator: function(value) {
            let isNumber = value >= 0 && value <= 6;
            return isNumber || 'Not Valid'; 
        },
    }
};
</script>

<template>
<div>
    <v-icon class="up-icon" @click="ballVal++" large>mdi-arrow-up-bold</v-icon>
    <span 
        class="counter-field"
        :class="{ warn: ballVal===4, ready: ballVal===5, stop: ballVal>5 }"
    >
        {{ballVal}}
    </span>
    <v-icon class="down-icon" @click="ballVal--" large>mdi-arrow-down-bold</v-icon>
        <span class="counter-field">
        Balls
        </span>
<!--
    <v-text-field 
        class="counter-field" 
        height="90px"
        :rules="[this.validator]" 
        label="Ball Count"
        v-model="ballVal"
    ></v-text-field> 
-->
</div>
</template>

<style scoped lang="scss">

.up-icon {
    margin-top: -30px;
    cursor: pointer;
}
.down-icon {
    margin-top: -20px;
    cursor: pointer;
}
.counter-field {
    width: 70px;
    font-size: 56px;

    &.warn {
        color: #ffff3d;
    }
    &.ready {
        color: #009688;
    }
    &.stop {
        color: #f44336;
    }
}

</style>
