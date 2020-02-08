<script>
import { NetworkTables } from '../utils/networktables'

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
        }
    },
    components: {},

    data: () => ({ icon: 'football'}),
    methods: {
        activateFunction: function() {
            NetworkTables.putValue('/SmartDashboard/' + this.networkKey, true);
            console.log("FUNCTION ACTIVATED - ",  this.networkKey);
        },
    }
};
</script>

<template>
    <div>
        <v-btn
            class="auto-button"
            outlined
            color="primary"
            height="55"
            @click="activateFunction()">{{label}}</v-btn>
    </div>
</template>