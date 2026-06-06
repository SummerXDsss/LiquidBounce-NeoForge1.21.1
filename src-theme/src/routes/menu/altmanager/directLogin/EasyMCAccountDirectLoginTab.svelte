<script lang="ts">
    import Tab from "../../common/modal/Tab.svelte";
    import IconTextInput from "../../common/setting/IconTextInput.svelte";
    import ButtonSetting from "../../common/setting/ButtonSetting.svelte";
    import {browse, directLoginToEasyMCAccount} from "../../../../integration/rest";

    let token = "";
    $: disabled = validateToken(token);

    function validateToken(token: string) {
        return token.length === 0;
    }

    async function login() {
        if (disabled) {
            return;
        }
        await directLoginToEasyMCAccount(token);
    }
</script>

<Tab>
    <IconTextInput icon="user" title="令牌" bind:value={token}/>
    <ButtonSetting title="登录" {disabled} on:click={login} listenForEnter={true} inset={true}/>
    <ButtonSetting title="获取账号令牌" on:click={() => browse("EASYMC")} secondary={true}/>
</Tab>
