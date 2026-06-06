<script lang="ts">
    import "nouislider/dist/nouislider.css";
    import "./nouislider.scss";
    import {createEventDispatcher, onMount} from "svelte";
    import noUiSlider, {type API} from "nouislider";
    import type {FloatSetting, ModuleSetting,} from "../../../integration/types";
    import ValueInput from "./common/ValueInput.svelte";
    import {spaceSeperatedNames} from "../../../theme/theme_config";
    import {translateSettingName} from "../../../theme/localization";

    export let setting: ModuleSetting;

    const cSetting = setting as FloatSetting;

    const dispatch = createEventDispatcher();

    let slider: HTMLElement;
    let apiSlider: API;

    onMount(() => {
        apiSlider = noUiSlider.create(slider, {
            start: cSetting.value,
            connect: "lower",
            range: {
                min: cSetting.range.from,
                max: cSetting.range.to,
            },
            step: 0.01,
        });

        apiSlider.on("update", (values) => {
            const newValue = parseFloat(values[0].toString());

            cSetting.value = newValue;
            setting = { ...cSetting };
            dispatch("change");
        });
    });
</script>

<div class="setting" class:has-suffix={cSetting.suffix !== ""}>
    <div class="name">{translateSettingName(cSetting.name, $spaceSeperatedNames)}</div>
    <div class="value">
        <ValueInput valueType="float" value={cSetting.value}
                    on:change={(e) => apiSlider.set(e.detail.value)}/>
    </div>
    {#if cSetting.suffix !== ""}
        <div class="suffix">{cSetting.suffix}</div>
    {/if}
    <div bind:this={slider} class="slider"></div>
</div>

<style lang="scss">
    @import "../../../colors.scss";

    .setting {
        padding: 7px 0 2px 0;
        display: grid;
        grid-template-areas:
            "a b"
            "d d";
        grid-template-columns: minmax(0, 1fr) max-content;
        column-gap: 5px;

        /* animation fix */
        min-height: 46px;
    }

    .setting.has-suffix {
        grid-template-areas:
            "a b c"
            "d d d";
        grid-template-columns: minmax(0, 1fr) max-content max-content;
    }

    .suffix,
    .setting {
        color: $clickgui-text-color;
        font-weight: 500;
        font-size: 12px;
    }

    .name {
        grid-area: a;
        font-weight: 500;
        min-width: 0;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .value {
        grid-area: b;
    }

    .suffix {
        grid-area: c;
    }

    .slider {
        grid-area: d;
        padding-right: 10px;
    }
</style>
