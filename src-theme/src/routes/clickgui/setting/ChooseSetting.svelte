<script lang="ts">
    import { createEventDispatcher } from "svelte";
    import type {
        ModuleSetting,
        ChooseSetting,
    } from "../../../integration/types";
    import Dropdown from "./common/Dropdown.svelte";
    import {spaceSeperatedNames} from "../../../theme/theme_config";
    import {translateSettingName} from "../../../theme/localization";

    export let setting: ModuleSetting;

    const cSetting = setting as ChooseSetting;

    const dispatch = createEventDispatcher();

    function handleChange() {
        setting = { ...cSetting };
        dispatch("change");
    }
</script>

<div class="setting">
    <Dropdown
        on:change={handleChange}
        bind:value={cSetting.value}
        options={cSetting.choices}
        name={translateSettingName(cSetting.name, $spaceSeperatedNames)}
    />
</div>

<style lang="scss">
    .setting {
        padding: 7px 0px;
    }
</style>
