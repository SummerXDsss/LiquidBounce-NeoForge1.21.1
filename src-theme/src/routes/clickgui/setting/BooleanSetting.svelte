<script lang="ts">
    import { createEventDispatcher } from "svelte";
    import type {
        ModuleSetting,
        BooleanSetting,
    } from "../../../integration/types";
    import Switch from "./common/Switch.svelte";
    import {spaceSeperatedNames} from "../../../theme/theme_config";
    import {translateSettingName} from "../../../theme/localization";

    export let setting: ModuleSetting;

    const cSetting = setting as BooleanSetting;

    const dispatch = createEventDispatcher();

    function handleChange() {
        setting = { ...cSetting };

        dispatch("change");
    }
</script>

<div class="setting">
    <Switch
        name={translateSettingName(cSetting.name, $spaceSeperatedNames)}
        bind:value={cSetting.value}
        on:change={handleChange}
    />
</div>

<style lang="scss">
    .setting {
        padding: 7px 0px;
    }
</style>
