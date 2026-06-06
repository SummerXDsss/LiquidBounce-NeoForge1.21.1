<script lang="ts">
    import Tabs from "../../common/modal/Tabs.svelte";
    import Modal from "../../common/modal/Modal.svelte";
    import CrackedAccountDirectLoginTab from "./CrackedAccountDirectLoginTab.svelte";
    import SessionAccountDirectLoginTab from "./SessionAccountDirectLoginTab.svelte";
    import { setItem } from "../../../../integration/persistent_storage";
    import EasyMCAccountDirectLoginTab from "./EasyMCAccountDirectLoginTab.svelte";

    export let visible: boolean;

    const tabs = [
        {
            title: "EasyMC",
            icon: "icon-easymc.svg",
            component: EasyMCAccountDirectLoginTab
        },
        {
            title: "离线",
            icon: "icon-cracked.png",
            component: CrackedAccountDirectLoginTab
        },
        {
            title: "会话",
            icon: "icon-session.svg",
            component: SessionAccountDirectLoginTab
        }
    ];

    let activeTab = parseInt(localStorage.getItem("altmanager_direct_login_active_tab") ?? "0");

    async function handleChangeTab(e: CustomEvent<{ activeTab: number }>) {
        activeTab = e.detail.activeTab;
        await setItem("altmanager_direct_login_active_tab", e.detail.activeTab.toString());
    }
</script>

<Modal title="直接登录" bind:visible={visible}>
    <Tabs {tabs} {activeTab} on:changeTab={handleChangeTab}/>
</Modal>
