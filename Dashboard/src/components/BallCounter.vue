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
    <v-text-field :rules="[this.validator]" label="Balls" v-model="ballVal"></v-text-field>
</div>
</template>

<style scoped lang="scss">

.icon {
    margin-right: 10px;
}

</style>
