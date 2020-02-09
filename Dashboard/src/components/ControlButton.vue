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