<script>
import { NetworkTables } from '../utils/networktables'

/**
 *  Create a button based on inputted props that sends a signal to the robot
 *  
 *  Inputs: 
 *      label: string - Text for the button
 *      networkKey: string - The network key your robot expects (this value is appended to '/SmartDashboard/')
 *      togglable: boolean - True if the button has an on/off state
 *
 *  Network key: /SmartDashboard/*
 */
export default {
    name: 'ControlButton',
    props: {
        label: {
            type: String,
            required: true
        },
         networkKey: {
            type: String,
            required: true
        },
        togglable: Boolean
    },

    data: () => ({
        toggled: false
    }),

    mounted: function() {
        NetworkTables.getValue('/SmartDashboard/' + this.networkKey, false);
    },

    methods: {
        activateFunction: function() {
            if (this.togglable) {
                this.toggled = !this.toggled;
            }
            NetworkTables.putValue('/SmartDashboard/' + this.networkKey, true);
        },
    }
};
</script>

<template>
    <div>
        <v-btn
            class="control-button"
            color="primary"
            height="55"
            :input-value="toggled"
            outlined
            @click="activateFunction()">
            
                {{label}}
        </v-btn>
    </div>
</template>


<style scoped lang="scss">
    .toggled-on {
        background-color: black;
    }
    
    .control-button {
        margin-left: 20px;
        margin-bottom: 3px;
    }
</style>