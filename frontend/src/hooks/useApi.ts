import React, { useContext, useEffect, useRef, useState } from 'react';
import {getApi} from '../axios/axios.ts';
import { ReloadApiContext } from './reload-api/ReloadApiContext.tsx';

export interface ApiData<T> {
    data: T | null | undefined
    loaded: boolean
    error: boolean | Error
    setData: React.Dispatch<any>
}

/**
 * Custom hook to fetch data from the API and return the data, whether it has been loaded or not, and any errors
 *
 * @param path the path to the API endpoint
 * @param method the HTTP method to use
 * @param payload the optional payload to send to the API
 */
export default function useApi<DataType = any>(path: string, method: string, payload?: any): ApiData<DataType> {
    // state for the data returned from the API and the error if there is one
    const [data, setData] = useState<DataType | null | undefined>(undefined);
    const [error, setError] = useState(false as boolean | Error);

    // get the reload token from the context to reload the API when the token changes
    const reloadToken = useContext(ReloadApiContext)?.reloadToken

    // ref to keep track of whether the data has been loaded or not
    const isLoaded = useRef(false);

    // fetch the data from the API
    useEffect(() => {
        isLoaded.current = false;
        console.log({path, method}); // print the path and method to the console for debugging
        (getApi() as any)[method](path, payload)
            .then((res: any) => {
                setData(res.data);
                setError(false)
                isLoaded.current = true;
            })
            .catch((e: Error) => {
                console.log(e)
                setData(null)
                setError(e);
                isLoaded.current = true;
            })
    }, [path, method, payload, reloadToken]);

    return {loaded: isLoaded.current, data: isLoaded.current ? data : undefined, error, setData};
}