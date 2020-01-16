

<script>
import DriverPage from './pages/driver.page';
import DetailedPage from './pages/detailed.page';
import { NetworkTables } from './utils/networktables'
// import * as logger from './utils/logger'

export default {
    name: 'App',

    components: {
        DetailedPage,
        DriverPage
    },

    data: () => ({
        drawer: null,
        currentPage: "Driver",
        bottomNav: 'recent'
    }),
    methods: {
        menuClick: function(page){
            this.currentPage = page;
            this.drawer = !this.drawer;
        },
        toggleBtnClicked: function() {
            NetworkTables.putValue('toggleRovot', 1);
        }
    },
};
</script>

<template>
<v-app>
    <v-navigation-drawer v-model="drawer" app>
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

    <v-bottom-navigation v-model="bottomNav" class="bottom-nav">
        <v-btn value="recent" @click="toggleBtnClicked()">
            <span>Toggle Robot</span>
            <v-icon>mdi-power</v-icon>
        </v-btn>

        <v-btn value="favorites">
            <span>Shoot Back</span>
            <v-icon>mdi-arrow-down-bold-hexagon-outline</v-icon>
        </v-btn>

        <v-btn value="nearby">
            <span>Shoot Forward</span>
            <v-icon>mdi-arrow-up-bold-hexagon-outline</v-icon>
        </v-btn>
    </v-bottom-navigation>
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