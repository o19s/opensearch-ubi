const path = require('path');
const {CleanWebpackPlugin} = require('clean-webpack-plugin');
const { type } = require('os');

module.exports = (env, argv) => {
	const {mode} = argv;
	return {
        entry: path.resolve(__dirname, "./ts/index.ts"),
        target: "web",
		mode,
        module: {
            rules: [{
                test: /^.+\.([tj])s$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: [
                                '@babel/preset-typescript'
                            ]
                    }
                }
            }]
        },
        resolve: {
			extensions: [".ts"],

		},
        output: {
            library: {
                name:'ubi-events',
                type:'global'

            },
			filename: "ubi_events.js",
			path: path.resolve(__dirname, 'js'),
		},
		plugins: [
			new CleanWebpackPlugin()
		]
    };
};