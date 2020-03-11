<script>
import { NetworkTables } from '../utils/networktables'

/**
 *  Shows how many Balls are in the Intake.
 *  Changes color based for warning when nearing the max ball count
 *  Up and down arrows to change the ball count in the robot.
 *
 *  Network key: /SmartDashboard/ballCounter
 */
export default {
    name: 'BallCouter',

    components: {},
    mounted:function(){
        NetworkTables.addKeyListener(this.networkKey, this.ballUpdate);
        this.ballVal = NetworkTables.getValue(this.networkKey, 0)
    },
    
    data: () => (
        {
            ballVal: 0,
            warnBallCount: 4,
            maxBallCount: 5,
            networkKey: '/SmartDashboard/ballCounter'
        }
    ),

    methods: {
        ballUpdate: function(key, value) {
            this.ballVal = value;
        },
    }
};
</script>

<template>
<div>
    <v-icon class="up-icon" @click="ballVal++" large>mdi-arrow-up-bold</v-icon>
    <span 
        class="counter-field"
        :class="{ warn: ballVal===warnBallCount, ready: ballVal===maxBallCount, stop: ballVal>maxBallCount }"
    >
        {{ballVal}}
    </span>
    <v-icon class="down-icon" @click="ballVal--" large>mdi-arrow-down-bold</v-icon>
    <span class="counter-field">
        Balls
    </span>
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
