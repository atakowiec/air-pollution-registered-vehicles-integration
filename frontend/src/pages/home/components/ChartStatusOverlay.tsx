import {ApiData} from "../../../hooks/useApi.ts";
import style from "../Home.module.scss";
import {isEmpty} from "../../../util/utils.ts";
import LoadingSpinner from "../../../components/LoadingSpinner.tsx";

export interface PropsWithApiData {
  apiData: ApiData;
}

interface Props extends PropsWithApiData {
  backgroundType?: "none" | "visible"
}

/**
 * Component that displays the status of the chart data
 * e.g. if the data is being loaded or failed to load
 *
 * @param apiData - data from the API that is used to determine the status of the chart data
 * @param backgroundType - type of the background that should be displayed
 */
export default function ChartStatusOverlay({apiData, backgroundType = "visible"}: Props) {
  // if data is loaded correctly, we don't need to display anything
  if (apiData.loaded && apiData.data && !isEmpty(apiData.data))
    return null;

  return (
    <div className={`${style.chartStatusOverlay} ${style[`background_${backgroundType}`]}`}>
      {apiData.error ? "Wystąpił błąd podczas ładowania danych"
        : !apiData.loaded ? <LoadingSpinner/>
          : isEmpty(apiData.data) ? "Brak danych do wyświetlenia" : ""}
    </div>
  )
}