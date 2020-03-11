<script>
import { NetworkTables } from '../utils/networktables'

/**
 *  Create an indicator based on inputted props to show a state of the robot
 *  
 *  Inputs: 
 *      label: string - Text for the indicator
 *      icon: string - Material design Icon name (this value is appended to 'mdi-')
 *      status: boolean - Optional. For complex indicators you can pass in the status of the indicator
 *      networkKey: string - Optional. The network key your robot will send the state to. Leave out if using status
 *      warn: boolean - Optional. The true state turns orange instead of green
 *      spin: boolean - Optional. The true state spins the icon
 *
 *  Network key: /SmartDashboard/*
 */
export default {
    name: 'Indicator',

    components: {},
    mounted:function(){
        if (this.networkKey) {
            NetworkTables.addKeyListener('/SmartDashboard/' + this.networkKey, this.updateIndicator);
        }
    },
    data: function () { 
        return { 
            toggledOn: this.status,
            activeColor: this.warn ? '#f44336' : '#00ff4e'
        }
    },
    props: {
        label: {
            type: String,
            required: true
        },
        icon: {
            type: String,
            required: true
        },
        networkKey: String,
        status: {
            type: Boolean
        },
        warn: Boolean,
        spin: Boolean
    },
    methods: {
        updateIndicator: function(key, newValue) {
            this.toggledOn = newValue;
        },
    }
};
</script>

<template>
<div>
    <v-icon 
        class="icon"
        v-bind:class="{'mdi-spin': status && spin}"
        v-bind:color="(status ? status : toggledOn) ? activeColor : 'white'"
        x-large>
            mdi-{{icon}}
    </v-icon>
    <span v-bind:class="{ active: status ? status : toggledOn, warn: warn }">{{label}}</span>
</div>
</template>

<style scoped lang="scss">

.icon {
    margin-right: 10px;
}

.active {
    color: #00ff4e;

    &.warn {
        color: #f44336
    }
}

</style>
