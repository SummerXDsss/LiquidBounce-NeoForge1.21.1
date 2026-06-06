<script lang="ts">
    import Tab from "../../common/modal/Tab.svelte";
    import IconTextInput from "../../common/setting/IconTextInput.svelte";
    import ButtonSetting from "../../common/setting/ButtonSetting.svelte";
    import {directLoginToCrackedAccount} from "../../../../integration/rest";
    import IconButton from "../../common/buttons/IconButton.svelte";
    import {faker} from "@faker-js/faker";
    import SwitchSetting from "../../common/setting/SwitchSetting.svelte";

    let username = "";
    let online = false;

    async function login() {
        await directLoginToCrackedAccount(username, online);
    }

    function generateRandomUsername() {
        username = faker.internet.userName().substring(0, 16).replace(/[^a-zA-Z0-9_]+/gi, "");
    }
</script>

<Tab>
    <IconTextInput icon="user" title="用户名" pattern={"[a-zA-Z0-9_]{1,16}"} bind:value={username} maxLength={16}>
        <IconButton icon="random" title="随机" on:click={generateRandomUsername}/>
    </IconTextInput>
    <SwitchSetting title="使用在线 UUID" bind:value={online}/>
    <ButtonSetting title="登录" on:click={login} listenForEnter={true} inset={true}/>
</Tab>
