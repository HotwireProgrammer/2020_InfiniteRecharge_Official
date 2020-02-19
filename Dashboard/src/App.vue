

<script>
import DriverPage from './pages/driver.page';
import DetailedPage from './pages/detailed.page';
import { NetworkTables } from './utils/networktables'
// import * as logger from './utils/logger'

import Vue from 'vue';
import VueGoogleCharts from 'vue-google-charts'

Vue.use(VueGoogleCharts)

export default {
    name: 'App',

    components: {
        DetailedPage,
        DriverPage
    },

    data: () => ({
        drawer: null,
        currentPage: "Driver",
        bottomNav: 'recent',
        dataLoggingStarted: false
    }),
    methods: {
        menuClick: function(page) {
            this.currentPage = page;
            this.drawer = !this.drawer;
        }
    }
};
</script>

<template>
<v-app>
    <v-navigation-drawer v-model="drawer" app floating disable-resize-watcher temporary>
        <v-list-item class="nav-line" link @click="menuClick('Driver')">
            <v-icon>mdi-home</v-icon>
            <span class="nav-text">Driver</span>
        </v-list-item>
        <v-list-item class="nav-line" link @click="menuClick('Detailed')">
            <v-icon>mdi-contact-mail</v-icon>
            <span class="nav-text">Detailed</span>
        </v-list-item>
    </v-navigation-drawer>

    <v-app-bar app color="primary">
        <v-app-bar-nav-icon @click.stop="drawer = !drawer" />
        <v-toolbar-title>{{currentPage}} Dashboard</v-toolbar-title>
    </v-app-bar>

    <v-content>
        <DriverPage v-if="currentPage==='Driver'" />
        <DetailedPage v-if="currentPage==='Detailed'" />
    </v-content>
</v-app>
</template>

<style scoped lang="scss">
    .nav-line {
        padding: 0px 25px;
    }
    .nav-text {
        padding: 20px;
    }
    .bottom-nav {
        color: rgba(255, 255, 255, .5) !important;
    }
</style>